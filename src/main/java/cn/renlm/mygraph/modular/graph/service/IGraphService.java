package cn.renlm.mygraph.modular.graph.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

import cn.renlm.mygraph.dto.User;
import cn.renlm.mygraph.modular.graph.dto.GraphDto;
import cn.renlm.mygraph.modular.graph.entity.Graph;
import cn.renlm.plugins.MyResponse.Result;

/**
 * <p>
 * 图形设计 服务类
 * </p>
 *
 * @author RenLiMing(任黎明)
 * @since 2022-04-29
 */
public interface IGraphService extends IService<Graph> {

	/**
	 * 分页列表（我的图库）
	 * 
	 * @param page
	 * @param user
	 * @param form
	 * @return
	 */
	Page<GraphDto> findPage(Page<GraphDto> page, User user, GraphDto form);

	/**
	 * 更新基本信息
	 * 
	 * @param user
	 * @param form
	 * @return
	 */
	Result<?> updateBaseInfo(User user, GraphDto form);

	/**
	 * 保存编辑器
	 * 
	 * @param user
	 * @param form
	 * @return
	 */
	Result<Graph> saveEditor(User user, GraphDto form);

}
