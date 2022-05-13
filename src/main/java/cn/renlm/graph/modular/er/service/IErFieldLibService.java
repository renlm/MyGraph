package cn.renlm.graph.modular.er.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

import cn.renlm.graph.dto.User;
import cn.renlm.graph.modular.er.dto.ErFieldLibDto;
import cn.renlm.graph.modular.er.entity.ErFieldLib;

/**
 * <p>
 * ER模型-我的字段库 服务类
 * </p>
 *
 * @author Renlm
 * @since 2022-05-13
 */
public interface IErFieldLibService extends IService<ErFieldLib> {

	/**
	 * 分页列表
	 * 
	 * @param page
	 * @param user
	 * @param form
	 * @return
	 */
	Page<ErFieldLib> findPage(Page<ErFieldLib> page, User user, ErFieldLibDto form);

}
