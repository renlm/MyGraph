package cn.renlm.mygraph.modular.doc.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import cn.renlm.mygraph.dto.User;
import cn.renlm.mygraph.modular.doc.dto.DocCategoryShareDto;
import cn.renlm.mygraph.modular.doc.entity.DocCategoryShare;

/**
 * <p>
 * 文档分类-分享 Mapper 接口
 * </p>
 *
 * @author RenLiMing(任黎明)
 * @since 2022-06-16
 */
public interface DocCategoryShareMapper extends BaseMapper<DocCategoryShare> {

	/**
	 * 分页列表
	 * 
	 * @param page
	 * @param user
	 * @param projectIds
	 * @param form
	 * @return
	 */
	Page<DocCategoryShareDto> findDocPage(Page<DocCategoryShareDto> page, @Param("user") User user,
			@Param("projectIds") List<Long> projectIds, @Param("form") DocCategoryShareDto form);

}
