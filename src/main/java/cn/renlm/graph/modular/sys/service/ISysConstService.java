package cn.renlm.graph.modular.sys.service;

import com.baomidou.mybatisplus.extension.service.IService;

import cn.renlm.graph.modular.sys.entity.SysConst;

/**
 * <p>
 * 系统常量 服务类
 * </p>
 *
 * @author RenLiMing(任黎明)
 * @since 2022-04-29
 */
public interface ISysConstService extends IService<SysConst> {

	/**
	 * 根据编码获取系统常量值
	 * 
	 * @param code
	 * @return
	 */
	String getValue(String code);

}
