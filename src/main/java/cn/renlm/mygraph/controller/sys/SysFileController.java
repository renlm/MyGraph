package cn.renlm.mygraph.controller.sys;

import static cn.renlm.mygraph.amqp.ExcelExportQueue.EXCHANGE;
import static cn.renlm.mygraph.amqp.ExcelExportQueue.ROUTINGKEY;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import cn.renlm.mygraph.amqp.AmqpUtil;
import cn.renlm.mygraph.common.Role;
import cn.renlm.mygraph.dto.User;
import cn.renlm.mygraph.modular.sys.entity.SysFile;
import cn.renlm.mygraph.modular.sys.service.ISysFileService;
import cn.renlm.plugins.MyResponse.Datagrid;
import cn.renlm.plugins.MyResponse.Result;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * 文件
 * 
 * @author RenLiMing(任黎明)
 *
 */
@Controller
@RequestMapping("/sys/file")
public class SysFileController {

	@Autowired
	private ISysFileService iSysFileService;

	/**
	 * 列表页
	 * 
	 * @return
	 */
	@GetMapping
	public String file() {
		return "sys/file";
	}

	/**
	 * 分页数据（超级管理员能查看所有用户数据）
	 * 
	 * @param request
	 * @param authentication
	 * @param page
	 * @param form
	 * @return
	 */
	@ResponseBody
	@GetMapping("/ajax/page")
	public Datagrid<SysFile> page(HttpServletRequest request, Authentication authentication, Page<SysFile> page,
			SysFile form) {
		User user = (User) authentication.getPrincipal();
		Page<SysFile> data = iSysFileService.page(page, Wrappers.<SysFile>lambdaQuery().func(wrapper -> {
			wrapper.select(SysFile.class, field -> field.getTypeHandler() == null);
			wrapper.eq(SysFile::getDeleted, false);
			if (request.isUserInRole(Role.SUPER.name())) {
				if (StrUtil.isNotBlank(form.getCreatorUserId())) {
					wrapper.eq(SysFile::getCreatorUserId, form.getCreatorUserId());
				}
			} else {
				wrapper.eq(SysFile::getCreatorUserId, user.getUserId());
			}
			if (form.getCreatedAt() != null) {
				wrapper.ge(SysFile::getCreatedAt, form.getCreatedAt());
			}
			if (StrUtil.isNotBlank(form.getOriginalFilename())) {
				wrapper.like(SysFile::getOriginalFilename, form.getOriginalFilename());
			}
			wrapper.orderByDesc(SysFile::getId);
		}));
		return Datagrid.of(data);
	}

	/**
	 * 文件上传
	 * 
	 * @param request
	 * @param authentication
	 * @param file
	 * @return
	 */
	@ResponseBody
	@PostMapping("/upload")
	public Result<SysFile> page(HttpServletRequest request, Authentication authentication, MultipartFile file) {
		User user = (User) authentication.getPrincipal();
		try {
			SysFile sysFile = iSysFileService.upload(file.getOriginalFilename(), file.getBytes(), entity -> {
				entity.setCreatorUserId(user.getUserId());
				entity.setCreatorNickname(user.getNickname());
			});
			sysFile.setFileContent(null);
			return Result.success(sysFile);
		} catch (Exception e) {
			e.printStackTrace();
			return Result.error("出错了");
		}
	}

	/**
	 * 下载（带参inline为预览）
	 * 
	 * @param request
	 * @param response
	 * @param fileId
	 * @throws IOException
	 */
	@GetMapping("/download/{fileId}")
	public void download(HttpServletRequest request, HttpServletResponse response,
			@PathVariable("fileId") String fileId) throws IOException {
		boolean inline = request.getParameterMap().containsKey("inline");
		SysFile file = iSysFileService.getOne(Wrappers.<SysFile>lambdaQuery().eq(SysFile::getFileId, fileId));
		String filename = URLEncoder.encode(file.getOriginalFilename(), "UTF-8");
		String[] viewArr = { ".gif", ".jpg", ".jpeg", ".bmp", ".png", ".pdf", ".xsd", ".xml" };
		String openStyle = inline ? (StrUtil.endWithAny(filename, viewArr) ? "inline" : "attachment") : "attachment";
		response.setHeader("Content-Type", file.getFileType());
		response.setHeader("Content-Disposition", openStyle + ";fileName=" + filename);
		try (ServletOutputStream os = response.getOutputStream()) {
			os.write(file.getFileContent());
		}
	}

	/**
	 * 创建表格文件导出任务
	 * 
	 * @param request
	 * @param authentication
	 * @param sysFile
	 * @return
	 */
	@ResponseBody
	@PostMapping("/createExcelExportTask")
	public Result<SysFile> createExcelExportTask(HttpServletRequest request, Authentication authentication,
			SysFile sysFile) {
		User user = (User) authentication.getPrincipal();
		String originalFilename = sysFile.getOriginalFilename();
		if (StrUtil.isBlank(originalFilename)) {
			return Result.error("文件名不能为空");
		}
		try {
			sysFile.setFileId(IdUtil.simpleUUID().toUpperCase());
			sysFile.setOriginalFilename(StrUtil.addSuffixIfNot(originalFilename, ".xlsx"));
			sysFile.setIsPublic(false);
			sysFile.setFileType(FileUtil.getMimeType(sysFile.getOriginalFilename()));
			sysFile.setStatus(2);
			sysFile.setCreatorUserId(user.getUserId());
			sysFile.setCreatorNickname(user.getNickname());
			sysFile.setCreatedAt(new Date());
			sysFile.setDeleted(false);
			iSysFileService.save(sysFile);
			AmqpUtil.createQueue(EXCHANGE, ROUTINGKEY, sysFile.getFileId());
			return Result.success(sysFile).setMessage("任务已添加，请在 [ 文件管理 ] 查看");
		} catch (Exception e) {
			e.printStackTrace();
			return Result.error("出错了");
		}
	}

}
