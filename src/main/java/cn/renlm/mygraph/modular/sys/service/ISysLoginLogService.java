package cn.renlm.mygraph.modular.sys.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

import cn.renlm.mygraph.modular.sys.entity.SysLoginLog;

/**
 * <p>
 * 系统登录日志 服务类
 * </p>
 *
 * @author RenLiMing(任黎明)
 * @since 2022-05-16
 */
public interface ISysLoginLogService extends IService<SysLoginLog> {

	/**
	 * 分页列表
	 * 
	 * @param page
	 * @param form
	 * @return
	 */
	Page<SysLoginLog> findPage(Page<SysLoginLog> page, SysLoginLog form);

}
