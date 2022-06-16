package cn.renlm.graph.modular.markdown.service.impl;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.renlm.graph.dto.User;
import cn.renlm.graph.modular.doc.entity.DocCategory;
import cn.renlm.graph.modular.doc.service.IDocCategoryService;
import cn.renlm.graph.modular.doc.service.IDocProjectService;
import cn.renlm.graph.modular.markdown.entity.Markdown;
import cn.renlm.graph.modular.markdown.entity.MarkdownHistory;
import cn.renlm.graph.modular.markdown.mapper.MarkdownMapper;
import cn.renlm.graph.modular.markdown.service.IMarkdownHistoryService;
import cn.renlm.graph.modular.markdown.service.IMarkdownService;
import cn.renlm.graph.response.Result;

/**
 * <p>
 * Markdown文档 服务实现类
 * </p>
 *
 * @author Renlm
 * @since 2022-05-13
 */
@Service
public class MarkdownServiceImpl extends ServiceImpl<MarkdownMapper, Markdown> implements IMarkdownService {

	@Autowired
	private IDocProjectService iDocProjectService;

	@Autowired
	private IDocCategoryService iDocCategoryService;

	@Autowired
	private IMarkdownHistoryService iMarkdownHistoryService;

	@Override
	@Transactional(rollbackFor = Exception.class)
	public Result<Markdown> ajaxSave(User user, Markdown form) {
		if (StrUtil.isBlank(form.getUuid())) {
			return Result.of(HttpStatus.BAD_REQUEST, "参数不全");
		} else {
			DocCategory docCategory = iDocCategoryService
					.getOne(Wrappers.<DocCategory>lambdaQuery().eq(DocCategory::getUuid, form.getUuid()));
			if (ObjectUtil.isNotEmpty(docCategory)) {
				Integer role = iDocProjectService.findRole(user, docCategory.getDocProjectId());
				if (role == null || !ArrayUtil.contains(new Integer[] { 2, 3 }, role.intValue())) {
					return Result.of(HttpStatus.FORBIDDEN, "您没有操作权限");
				}
			}
			Markdown entity = this.getOne(Wrappers.<Markdown>lambdaQuery().eq(Markdown::getUuid, form.getUuid()));
			if (entity == null) {
				if (form.getSource() == null || !ArrayUtil.contains(new Integer[] { 1, 2, 3 }, form.getSource())) {
					return Result.of(HttpStatus.BAD_REQUEST, "来源不明");
				}
				form.setUuid(form.getUuid());
				form.setVersion(1);
				form.setCreatedAt(new Date());
				form.setCreatorUserId(user.getUserId());
				form.setCreatorNickname(user.getNickname());
				form.setUpdatedAt(form.getCreatedAt());
				form.setDeleted(false);
			} else {
				if (form.getVersion() == null) {
					return Result.of(HttpStatus.BAD_REQUEST, "缺失版本号");
				} else if (form.getVersion().intValue() < entity.getVersion().intValue()) {
					return Result.of(HttpStatus.BAD_REQUEST, "版本错误，请获取最新数据");
				}
				form.setId(entity.getId());
				form.setSource(entity.getSource());
				form.setVersion(entity.getVersion() + 1);
				form.setCreatedAt(entity.getCreatedAt());
				form.setCreatorUserId(entity.getCreatorUserId());
				form.setCreatorNickname(entity.getCreatorNickname());
				form.setUpdatedAt(new Date());
				form.setUpdatorUserId(user.getUserId());
				form.setUpdatorNickname(user.getNickname());
				form.setDeleted(entity.getDeleted());
			}
		}
		this.saveOrUpdate(form);
		// 历史记录
		MarkdownHistory history = BeanUtil.copyProperties(form, MarkdownHistory.class);
		history.setId(null);
		history.setMarkdownUuid(form.getUuid());
		iMarkdownHistoryService.save(history);
		return Result.success(form);
	}
}
