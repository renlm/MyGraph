package cn.renlm.mygraph.modular.markdown.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

import cn.renlm.mygraph.dto.User;
import cn.renlm.mygraph.modular.markdown.dto.MarkdownHistoryDto;
import cn.renlm.mygraph.modular.markdown.entity.MarkdownHistory;

/**
 * <p>
 * Markdown文档-历史记录 服务类
 * </p>
 *
 * @author RenLiMing(任黎明)
 * @since 2022-06-13
 */
public interface IMarkdownHistoryService extends IService<MarkdownHistory> {

	/**
	 * 分页列表（文档）
	 * 
	 * @param page
	 * @param user
	 * @param form
	 * @return
	 */
	Page<MarkdownHistoryDto> findDocPage(Page<MarkdownHistoryDto> page, User user, MarkdownHistoryDto form);

}
