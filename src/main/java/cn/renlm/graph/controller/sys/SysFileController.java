package cn.renlm.graph.controller.sys;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.Date;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import cn.renlm.graph.amqp.AmqpUtil;
import cn.renlm.graph.amqp.ExcelExportQueueConfig;
import cn.renlm.graph.common.FileStorage;
import cn.renlm.graph.common.Result;
import cn.renlm.graph.common.Role;
import cn.renlm.graph.modular.sys.entity.SysFile;
import cn.renlm.graph.modular.sys.service.ISysFileService;
import cn.renlm.graph.security.User;

/**
 * 文件
 * 
 * @author Renlm
 *
 */
@Controller
@RequestMapping("/sys/file")
public class SysFileController {

	@Autowired
	private ISysFileService iSysFileService;

	/**
	 * 本地文件
	 * 
	 * @return
	 */
	@RequestMapping
	public String file() {
		return "sys/file";
	}

	/**
	 * 文件列表（超级管理员能查看所有用户数据）
	 * 
	 * @param request
	 * @param authentication
	 * @param page
	 * @param sysFile
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/ajax/page")
	public Page<SysFile> page(HttpServletRequest request, Authentication authentication, Page<SysFile> page,
			SysFile sysFile) {
		User user = (User) authentication.getPrincipal();
		return iSysFileService.page(page, Wrappers.<SysFile>lambdaQuery().func(wrapper -> {
			wrapper.select(SysFile.class, field -> !field.getPropertyType().isArray());
			wrapper.eq(SysFile::getDeleted, false);
			if (request.isUserInRole(Role.SUPER.name())) {
				if (StrUtil.isNotBlank(sysFile.getCreatorUserId())) {
					wrapper.eq(SysFile::getCreatorUserId, sysFile.getCreatorUserId());
				}
			} else {
				wrapper.eq(SysFile::getCreatorUserId, user.getUserId());
			}
			if (sysFile.getCreatedAt() != null) {
				wrapper.ge(SysFile::getCreatedAt, sysFile.getCreatedAt());
			}
			if (StrUtil.isNotBlank(sysFile.getOriginalFilename())) {
				wrapper.like(SysFile::getOriginalFilename, sysFile.getOriginalFilename());
			}
			wrapper.orderByDesc(SysFile::getCreatedAt);
		}));
	}

	/**
	 * 文件上传
	 * 
	 * @param authentication
	 * @param file
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/upload")
	public Result<?> page(Authentication authentication, MultipartFile file) {
		User user = (User) authentication.getPrincipal();
		try {
			SysFile sysFile = iSysFileService.upload(FileStorage.dbPub, file.getOriginalFilename(), file.getBytes(),
					entity -> {
						entity.setCreatorUserId(user.getUserId());
						entity.setCreatorNickname(user.getNickname());
					});
			return Result.success(sysFile.getFileId()).setMessage("上传成功");
		} catch (Exception e) {
			e.printStackTrace();
			return Result.error("服务器出错了");
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
	@RequestMapping("/download/{fileId}")
	public void download(HttpServletRequest request, HttpServletResponse response,
			@PathVariable("fileId") String fileId) throws IOException {
		boolean inline = request.getParameterMap().containsKey("inline");
		String openStyle = inline ? "inline" : "attachment";
		SysFile file = iSysFileService.getById(fileId);
		String filename = URLEncoder.encode(file.getOriginalFilename(), "UTF-8");
		response.setHeader("Content-Type", file.getFileType());
		response.setHeader("Content-Disposition", openStyle + ";fileName=" + filename);
		ServletOutputStream os = response.getOutputStream();
		os.write(file.getFileContent());
		IoUtil.close(os);
	}

	/**
	 * 创建表格文件导出任务
	 * 
	 * @param request
	 * @param sysFile
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/createExcelDataExportTask")
	public Result<?> createExcelDataExportTask(Authentication authentication, SysFile sysFile) {
		User user = (User) authentication.getPrincipal();
		String originalFilename = sysFile.getOriginalFilename();
		if (StrUtil.isBlank(originalFilename)) {
			return Result.error().setMessage("文件名不能为空");
		}
		try {
			sysFile.setFileId(IdUtil.simpleUUID().toUpperCase());
			sysFile.setOriginalFilename(
					StrUtil.endWith(originalFilename, ".xlsx") ? originalFilename : (originalFilename + ".xlsx"));
			sysFile.setFileType(FileUtil.getMimeType(sysFile.getOriginalFilename()));
			sysFile.setStorage(FileStorage.dbPri.getType());
			sysFile.setIsPublic(FileStorage.dbPri.isPublic());
			sysFile.setStatus(2);
			sysFile.setCreatorUserId(user.getUserId());
			sysFile.setCreatorNickname(user.getNickname());
			sysFile.setCreatedAt(new Date());
			sysFile.setDeleted(false);
			iSysFileService.save(sysFile);
			AmqpUtil.createQueue(ExcelExportQueueConfig.EXCHANGE, ExcelExportQueueConfig.ROUTINGKEY,
					sysFile.getFileId());
			return Result.success().setMessage("任务已添加，请在 [ 文件管理->文件列表 ] 查看");
		} catch (Exception e) {
			e.printStackTrace();
			return Result.error("服务器出错了");
		}
	}
}