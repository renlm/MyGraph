package cn.renlm.graph.modular.markdown.service.impl;

import java.util.Date;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import cn.hutool.core.util.StrUtil;
import cn.renlm.graph.dto.User;
import cn.renlm.graph.modular.markdown.entity.Markdown;
import cn.renlm.graph.modular.markdown.mapper.MarkdownMapper;
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

	@Override
	@Transactional(rollbackFor = Exception.class)
	public Result<Markdown> ajaxSave(User user, Markdown form) {
		if (StrUtil.isBlank(form.getUuid())) {
			return Result.of(HttpStatus.BAD_REQUEST, "参数不全");
		} else {
			Markdown entity = this.getOne(Wrappers.<Markdown>lambdaQuery().eq(Markdown::getUuid, form.getUuid()));
			if (entity == null) {
				form.setUuid(form.getUuid());
				form.setVersion(1);
				form.setCreatedAt(new Date());
				form.setCreatorUserId(user.getUserId());
				form.setCreatorNickname(user.getNickname());
				form.setUpdatedAt(form.getCreatedAt());
				form.setDeleted(false);
			} else {
				form.setId(entity.getId());
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
		return Result.success(form);
	}
}
