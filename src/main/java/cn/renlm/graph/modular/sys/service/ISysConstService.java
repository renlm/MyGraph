package cn.renlm.graph.modular.sys.service;

import com.baomidou.mybatisplus.extension.service.IService;

import cn.renlm.graph.modular.sys.entity.SysConst;

/**
 * <p>
 * 系统常量 服务类
 * </p>
 *
 * @author Renlm
 * @since 2020-11-11
 */
public interface ISysConstService extends IService<SysConst> {

	/**
	 * 根据编码获取系统常量值
	 * 
	 * @param code
	 * @return
	 */
	String val(String code);

}
