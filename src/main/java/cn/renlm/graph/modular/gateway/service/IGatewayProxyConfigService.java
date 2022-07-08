package cn.renlm.graph.modular.gateway.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

import cn.renlm.graph.dto.User;
import cn.renlm.graph.modular.gateway.dto.GatewayProxyConfigDto;
import cn.renlm.graph.modular.gateway.entity.GatewayProxyConfig;
import cn.renlm.graph.response.Result;

/**
 * <p>
 * 网关代理配置 服务类
 * </p>
 *
 * @author Renlm
 * @since 2022-07-01
 */
public interface IGatewayProxyConfigService extends IService<GatewayProxyConfig> {

	/**
	 * 分页列表
	 * 
	 * @param page
	 * @param user
	 * @param form
	 * @return
	 */
	Page<GatewayProxyConfig> findPage(Page<GatewayProxyConfig> page, User user, GatewayProxyConfigDto form);

	/**
	 * 保存（新建|编辑）
	 * 
	 * @param user
	 * @param form
	 * @return
	 */
	Result<GatewayProxyConfigDto> ajaxSave(User user, GatewayProxyConfigDto form);

}
