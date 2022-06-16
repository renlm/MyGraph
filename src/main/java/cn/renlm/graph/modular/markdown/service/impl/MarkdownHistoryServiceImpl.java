package cn.renlm.graph.modular.markdown.service.impl;

import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import cn.renlm.graph.dto.User;
import cn.renlm.graph.modular.markdown.dto.MarkdownHistoryDto;
import cn.renlm.graph.modular.markdown.entity.MarkdownHistory;
import cn.renlm.graph.modular.markdown.mapper.MarkdownHistoryMapper;
import cn.renlm.graph.modular.markdown.service.IMarkdownHistoryService;

/**
 * <p>
 * Markdown文档-历史记录 服务实现类
 * </p>
 *
 * @author Renlm
 * @since 2022-06-13
 */
@Service
public class MarkdownHistoryServiceImpl extends ServiceImpl<MarkdownHistoryMapper, MarkdownHistory>
		implements IMarkdownHistoryService {

	@Override
	public Page<MarkdownHistoryDto> findPage(Page<MarkdownHistoryDto> page, User user, MarkdownHistoryDto form) {
		return null;
	}
}
