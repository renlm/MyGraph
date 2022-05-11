package cn.renlm.graph.modular.graph.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

import cn.renlm.graph.dto.User;
import cn.renlm.graph.modular.graph.dto.GraphDto;
import cn.renlm.graph.modular.graph.entity.Graph;
import cn.renlm.graph.response.Result;

/**
 * <p>
 * 图形设计 服务类
 * </p>
 *
 * @author Renlm
 * @since 2022-04-29
 */
public interface IGraphService extends IService<Graph> {

	/**
	 * 分页列表
	 * 
	 * @param page
	 * @param form
	 * @return
	 */
	Page<Graph> findPage(Page<Graph> page, GraphDto form);

	/**
	 * 保存我的（新建|编辑）
	 * 
	 * @param user
	 * @param form
	 * @return
	 */
	Result<GraphDto> mineAjaxSave(User user, GraphDto form);

}
