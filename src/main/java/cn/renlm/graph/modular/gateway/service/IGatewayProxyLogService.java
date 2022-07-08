package cn.renlm.graph.modular.gateway.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

import cn.renlm.graph.dto.User;
import cn.renlm.graph.modular.gateway.dto.GatewayProxyLogDto;
import cn.renlm.graph.modular.gateway.entity.GatewayProxyLog;

/**
 * <p>
 * 网关代理日志 服务类
 * </p>
 *
 * @author Renlm
 * @since 2022-07-08
 */
public interface IGatewayProxyLogService extends IService<GatewayProxyLog> {

	/**
	 * 分页列表
	 * 
	 * @param page
	 * @param user
	 * @param form
	 * @return
	 */
	Page<GatewayProxyLog> findPage(Page<GatewayProxyLog> page, User user, GatewayProxyLogDto form);

}
