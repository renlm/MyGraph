package cn.renlm.graph.modular.sys.service.impl;

import java.util.Date;
import java.util.function.Consumer;

import org.apache.commons.io.FilenameUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.CharUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.URLUtil;
import cn.hutool.http.HttpUtil;
import cn.renlm.graph.common.FileStorage;
import cn.renlm.graph.modular.sys.entity.SysFile;
import cn.renlm.graph.modular.sys.mapper.SysFileMapper;
import cn.renlm.graph.modular.sys.service.ISysFileService;

/**
 * <p>
 * 文件 服务实现类
 * </p>
 *
 * @author Renlm
 * @since 2020-09-17
 */
@Service
@DS("master")
public class SysFileServiceImpl extends ServiceImpl<SysFileMapper, SysFile> implements ISysFileService {

	@Override
	@Transactional(rollbackFor = Exception.class)
	public SysFile upload(FileStorage storage, String url, Consumer<SysFile> file) {
		String originalFilename = FilenameUtils.getName(FilenameUtils.normalize(url, true));
		byte[] bytes = HttpUtil.downloadBytes(URLUtil.normalize(url));
		return this.upload(storage, originalFilename, bytes, file);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public SysFile upload(FileStorage storage, String originalFilename, byte[] bytes, Consumer<SysFile> file) {
		String formatDate = DateUtil.formatDate(new Date());

		// 文件信息
		SysFile entity = new SysFile();
		entity.setFileId(IdUtil.simpleUUID().toUpperCase());
		entity.setOriginalFilename(originalFilename);
		entity.setStorage(storage.getType());
		entity.setIsPublic(storage.isPublic());
		entity.setSize((long) bytes.length);
		entity.setStatus(1);
		entity.setCreatedAt(new Date());
		entity.setDeleted(false);

		// 文件信息
		file.accept(entity);
		entity.setFileType(FileUtil.getMimeType(entity.getOriginalFilename()));

		// 存储到数据库
		if (storage.isDb()) {
			entity.setFileContent(bytes);
		}
		// 存储到服务器
		else {
			String publicPath = StrUtil.nullToEmpty(storage.getPublicPath());
			String uri = publicPath + CharUtil.SLASH + formatDate + CharUtil.SLASH + IdUtil.simpleUUID().toUpperCase()
					+ CharUtil.DOT + FilenameUtils.getExtension(entity.getOriginalFilename());
			String filePath = storage.getFilePath() + uri;
			FileUtil.mkParentDirs(filePath);
			FileUtil.writeBytes(bytes, filePath);
			entity.setPath(filePath);
			if (storage.isPublic()) {
				entity.setUri(uri);
			}
		}

		// 插入记录
		this.save(entity);
		return entity;
	}
}
