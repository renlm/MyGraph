package cn.renlm.graph.modular.doc.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import cn.renlm.graph.modular.doc.dto.DocCategoryShareDto;
import cn.renlm.graph.modular.doc.entity.DocCategoryShare;

/**
 * <p>
 * 文档分类-分享 Mapper 接口
 * </p>
 *
 * @author Renlm
 * @since 2022-06-16
 */
public interface DocCategoryShareMapper extends BaseMapper<DocCategoryShare> {

	/**
	 * 分页列表
	 * 
	 * @param page
	 * @param projectIds
	 * @param form
	 * @return
	 */
	Page<DocCategoryShareDto> findDocPage(Page<DocCategoryShareDto> page, @Param("projectIds") List<Long> projectIds,
			@Param("form") DocCategoryShareDto form);

}
