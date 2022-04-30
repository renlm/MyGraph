package cn.renlm.graph.modular.sys.service;

import cn.renlm.graph.modular.sys.entity.SysConst;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 系统常量 服务类
 * </p>
 *
 * @author Renlm
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

	/**
	 * 是否启用账号注册
	 * 
	 * @return
	 */
	Boolean getCfgEnableRegistration();

}
