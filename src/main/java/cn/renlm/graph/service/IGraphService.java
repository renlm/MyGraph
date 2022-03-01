package cn.renlm.graph.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

import cn.renlm.graph.dto.GraphDto;
import cn.renlm.graph.entity.Graph;

/**
 * <p>
 * 图形设计 服务类
 * </p>
 *
 * @author Renlm
 * @since 2022-02-26
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

}
