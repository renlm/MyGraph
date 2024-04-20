package cn.renlm.mygraph.modular.sys.service;

import java.util.function.Consumer;

import com.baomidou.mybatisplus.extension.service.IService;

import cn.renlm.mygraph.modular.sys.entity.SysFile;

/**
 * <p>
 * 文件 服务类
 * </p>
 *
 * @author RenLiMing(任黎明)
 * @since 2022-04-29
 */
public interface ISysFileService extends IService<SysFile> {

	/**
	 * 上传文件
	 * 
	 * @param originalFilename
	 * @param bytes
	 * @param file
	 * @return
	 */
	SysFile upload(String originalFilename, byte[] bytes, Consumer<SysFile> file);

}
