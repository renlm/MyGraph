package cn.renlm.graph.controller.sys;

import static cn.renlm.crawler.config.AmqpConfig.ExcelDataExportTaskAmqp.excelDataExportTaskDirectExchange;
import static cn.renlm.crawler.config.AmqpConfig.ExcelDataExportTaskAmqp.excelDataExportTaskRoutingKey;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.Date;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
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
import cn.renlm.crawler.Result;
import cn.renlm.crawler.consts.FileStorage;
import cn.renlm.crawler.consts.Role;
import cn.renlm.crawler.sys.dto.User;
import cn.renlm.crawler.sys.entity.SysFile;
import cn.renlm.crawler.sys.service.ISysFileService;
import cn.renlm.crawler.utils.SpringSecurityUtil;

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
	private AmqpTemplate amqpTemplate;

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
	 * @param page
	 * @param sysFile
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/ajax/page")
	public Page<SysFile> page(HttpServletRequest request, Page<SysFile> page, SysFile sysFile) {
		User user = SpringSecurityUtil.getPrincipal(request, User.class);
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
	 * @param request
	 * @param file
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/upload")
	public Result page(HttpServletRequest request, MultipartFile file) {
		User user = SpringSecurityUtil.getPrincipal(request, User.class);
		try {
			SysFile sysFile = iSysFileService.upload(FileStorage.dbPub, file.getOriginalFilename(), file.getBytes(),
					entity -> {
						entity.setCreatorUserId(user.getUserId());
						entity.setCreatorNickname(user.getNickname());
					});
			return Result.success().setFilePath(sysFile.getFileId()).setMessage("上传成功");
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
	public Result createExcelDataExportTask(HttpServletRequest request, SysFile sysFile) {
		User user = SpringSecurityUtil.getPrincipal(request, User.class);
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
			amqpTemplate.convertAndSend(excelDataExportTaskDirectExchange, excelDataExportTaskRoutingKey,
					sysFile.getFileId());
			return Result.success().setMessage("任务已添加，请在 [ 文件管理->文件列表 ] 查看");
		} catch (Exception e) {
			e.printStackTrace();
			return Result.error("服务器出错了");
		}
	}
}