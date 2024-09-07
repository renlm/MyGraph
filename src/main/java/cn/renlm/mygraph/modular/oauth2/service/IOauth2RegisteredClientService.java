package cn.renlm.mygraph.modular.oauth2.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

import cn.renlm.mygraph.modular.oauth2.dto.Oauth2RegisteredClientDto;
import cn.renlm.mygraph.modular.oauth2.entity.Oauth2RegisteredClient;

/**
 * <p>
 * Oauth2 注册客户端 服务类
 * </p>
 *
 * @author RenLiMing(任黎明)
 * @since 2023-01-07
 */
public interface IOauth2RegisteredClientService extends IService<Oauth2RegisteredClient> {

	/**
	 * 分页列表
	 * 
	 * @param page
	 * @param form
	 * @return
	 */
	Page<Oauth2RegisteredClient> findPage(Page<Oauth2RegisteredClient> page, Oauth2RegisteredClientDto form);

}
