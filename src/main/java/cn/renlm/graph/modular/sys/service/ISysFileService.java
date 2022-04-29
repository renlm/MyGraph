package cn.renlm.graph.modular.sys.service;

import java.util.function.Consumer;

import com.baomidou.mybatisplus.extension.service.IService;

import cn.renlm.graph.common.FileStorage;
import cn.renlm.graph.modular.sys.entity.SysFile;

/**
 * <p>
 * 文件 服务类
 * </p>
 *
 * @author Renlm
 * @since 2020-09-17
 */
public interface ISysFileService extends IService<SysFile> {

	/**
	 * 上传文件
	 * 
	 * @param storage
	 * @param url
	 * @param file
	 * @return
	 */
	SysFile upload(FileStorage storage, String url, Consumer<SysFile> file);

	/**
	 * 上传文件
	 * 
	 * @param storage
	 * @param originalFilename
	 * @param bytes
	 * @param file
	 * @return
	 */
	SysFile upload(FileStorage storage, String originalFilename, byte[] bytes, Consumer<SysFile> file);

}
