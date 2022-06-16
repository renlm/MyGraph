package cn.renlm.graph.modular.doc.service.impl;

import java.util.Date;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.BooleanUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.asymmetric.KeyType;
import cn.hutool.crypto.asymmetric.RSA;
import cn.renlm.graph.dto.User;
import cn.renlm.graph.modular.doc.dto.DocCategoryShareDto;
import cn.renlm.graph.modular.doc.entity.DocCategory;
import cn.renlm.graph.modular.doc.entity.DocCategoryShare;
import cn.renlm.graph.modular.doc.entity.DocProject;
import cn.renlm.graph.modular.doc.mapper.DocCategoryShareMapper;
import cn.renlm.graph.modular.doc.service.IDocCategoryService;
import cn.renlm.graph.modular.doc.service.IDocCategoryShareService;
import cn.renlm.graph.modular.doc.service.IDocProjectService;
import cn.renlm.graph.response.Result;

/**
 * <p>
 * 文档分类-分享 服务实现类
 * </p>
 *
 * @author Renlm
 * @since 2022-06-16
 */
@Service
public class DocCategoryShareServiceImpl extends ServiceImpl<DocCategoryShareMapper, DocCategoryShare>
		implements IDocCategoryShareService {

	@Resource
	private RSA rsa;

	@Autowired
	private IDocProjectService iDocProjectService;

	@Autowired
	private IDocCategoryService iDocCategoryService;

	@Override
	public Result<?> closeShare(User user, String docProjectUuid, String docCategoryUuid) {
		return null;
	}

	@Override
	public Result<?> ajaxSave(User user, DocCategoryShareDto form) {
		DocProject docProject = iDocProjectService
				.getOne(Wrappers.<DocProject>lambdaQuery().eq(DocProject::getUuid, form.getDocProjectUuid()));
		if (ObjectUtil.isEmpty(docProject) || !BooleanUtil.isTrue(docProject.getIsShare())) {
			return Result.of(HttpStatus.FORBIDDEN, "您没有操作权限");
		}
		if (!BooleanUtil.isFalse(docProject.getDeleted())) {
			return Result.of(HttpStatus.FORBIDDEN, "项目已被删除");
		}
		DocCategory docCategory = iDocCategoryService
				.getOne(Wrappers.<DocCategory>lambdaQuery().eq(DocCategory::getUuid, form.getDocCategoryUuid()));
		if (ObjectUtil.isEmpty(docCategory)) {
			return Result.of(HttpStatus.FORBIDDEN, "您没有操作权限");
		}
		if (!BooleanUtil.isFalse(docCategory.getDeleted())) {
			return Result.of(HttpStatus.FORBIDDEN, "数据已被删除");
		}
		Integer role = iDocProjectService.findRole(user, docCategory.getDocProjectId());
		if (role == null) {
			return Result.of(HttpStatus.FORBIDDEN, "您没有操作权限");
		}
		// 保存分享信息
		String password = form.getPassword();
		if (NumberUtil.equals(form.getShareType(), 1)) {
			form.setPassword(null);
		} else {
			if (StrUtil.isBlank(password)) {
				return Result.of(HttpStatus.BAD_REQUEST, "访问密码缺失");
			}
			form.setPassword(rsa.encryptBase64(password, KeyType.PrivateKey));
		}
		if (!NumberUtil.equals(form.getEffectiveType(), -1)) {
			form.setDeadline(DateUtil.offsetDay(new Date(), form.getEffectiveType()));
		}
		form.setDocProjectId(docCategory.getDocProjectId());
		form.setDocCategoryId(docCategory.getId());
		form.setUuid(IdUtil.simpleUUID().toUpperCase());
		form.setCreatedAt(new Date());
		form.setCreatorUserId(user.getUserId());
		form.setCreatorNickname(user.getNickname());
		form.setUpdatedAt(form.getCreatedAt());
		form.setDisabled(false);
		form.setDeleted(false);
		this.save(form);
		return Result.success(form.getUuid());
	}

	@Override
	public Page<DocCategoryShareDto> findPage(Page<DocCategoryShareDto> page, User user, DocCategoryShareDto form) {
		return null;
	}
}
