package cn.renlm.graph.modular.markdown.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import cn.renlm.graph.modular.markdown.dto.MarkdownHistoryDto;
import cn.renlm.graph.modular.markdown.entity.MarkdownHistory;

/**
 * <p>
 * Markdown文档-历史记录 Mapper 接口
 * </p>
 *
 * @author RenLiMing(任黎明)
 * @since 2022-06-13
 */
public interface MarkdownHistoryMapper extends BaseMapper<MarkdownHistory> {

	/**
	 * 分页列表（文档）
	 * 
	 * @param page
	 * @param projectIds
	 * @param form
	 * @return
	 */
	Page<MarkdownHistoryDto> findDocPage(Page<MarkdownHistoryDto> page, @Param("projectIds") List<Long> projectIds,
			@Param("form") MarkdownHistoryDto form);

}
