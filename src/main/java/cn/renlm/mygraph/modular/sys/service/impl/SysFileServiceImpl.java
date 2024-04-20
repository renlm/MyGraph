package cn.renlm.mygraph.modular.sys.service.impl;

import java.util.Date;
import java.util.function.Consumer;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.IdUtil;
import cn.renlm.mygraph.modular.sys.entity.SysFile;
import cn.renlm.mygraph.modular.sys.mapper.SysFileMapper;
import cn.renlm.mygraph.modular.sys.service.ISysFileService;

/**
 * <p>
 * 文件 服务实现类
 * </p>
 *
 * @author RenLiMing(任黎明)
 * @since 2022-04-29
 */
@Service
public class SysFileServiceImpl extends ServiceImpl<SysFileMapper, SysFile> implements ISysFileService {

	@Override
	@Transactional(rollbackFor = Exception.class)
	public SysFile upload(String originalFilename, byte[] bytes, Consumer<SysFile> file) {
		// 基本信息
		SysFile entity = new SysFile();
		entity.setFileId(IdUtil.simpleUUID().toUpperCase());
		entity.setOriginalFilename(originalFilename);
		entity.setIsPublic(false);
		entity.setSize((long) bytes.length);
		entity.setStatus(1);
		entity.setCreatedAt(new Date());
		entity.setDeleted(false);

		// 文件信息
		file.accept(entity);
		entity.setFileType(FileUtil.getMimeType(entity.getOriginalFilename()));
		entity.setFileContent(bytes);

		// 插入记录
		this.save(entity);
		return entity;
	}
}
