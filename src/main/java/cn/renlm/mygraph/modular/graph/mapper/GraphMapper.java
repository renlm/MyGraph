package cn.renlm.mygraph.modular.graph.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import cn.renlm.mygraph.dto.User;
import cn.renlm.mygraph.modular.graph.dto.GraphDto;
import cn.renlm.mygraph.modular.graph.entity.Graph;

/**
 * <p>
 * 图形设计 Mapper 接口
 * </p>
 *
 * @author RenLiMing(任黎明)
 * @since 2022-04-29
 */
public interface GraphMapper extends BaseMapper<Graph> {

	/**
	 * 分页列表
	 * 
	 * @param page
	 * @param user
	 * @param projectIds
	 * @param form
	 * @return
	 */
	Page<GraphDto> findPage(Page<GraphDto> page, @Param("user") User user, @Param("projectIds") List<Long> projectIds,
			@Param("form") GraphDto form);

}
