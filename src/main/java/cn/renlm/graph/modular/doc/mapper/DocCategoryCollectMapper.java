package cn.renlm.graph.modular.doc.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import cn.renlm.graph.modular.doc.dto.DocCategoryCollectDto;
import cn.renlm.graph.modular.doc.entity.DocCategoryCollect;

/**
 * <p>
 * 文档分类-收藏 Mapper 接口
 * </p>
 *
 * @author Renlm
 * @since 2022-06-15
 */
public interface DocCategoryCollectMapper extends BaseMapper<DocCategoryCollect> {

	/**
	 * 分页列表
	 * 
	 * @param page
	 * @param projectIds
	 * @param form
	 * @return
	 */
	Page<DocCategoryCollectDto> findDocPage(Page<DocCategoryCollectDto> page,
			@Param("projectIds") List<Long> projectIds, @Param("form") DocCategoryCollectDto form);

}
