package cn.renlm.graph.modular.doc.service.impl;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import cn.renlm.graph.dto.User;
import cn.renlm.graph.modular.doc.dto.DocCategoryCollectDto;
import cn.renlm.graph.modular.doc.entity.DocCategory;
import cn.renlm.graph.modular.doc.entity.DocCategoryCollect;
import cn.renlm.graph.modular.doc.entity.DocProject;
import cn.renlm.graph.modular.doc.mapper.DocCategoryCollectMapper;
import cn.renlm.graph.modular.doc.service.IDocCategoryCollectService;
import cn.renlm.graph.modular.doc.service.IDocCategoryService;
import cn.renlm.graph.modular.doc.service.IDocProjectService;
import cn.renlm.graph.response.Result;

/**
 * <p>
 * 文档分类-收藏 服务实现类
 * </p>
 *
 * @author Renlm
 * @since 2022-06-15
 */
@Service
public class DocCategoryCollectServiceImpl extends ServiceImpl<DocCategoryCollectMapper, DocCategoryCollect>
		implements IDocCategoryCollectService {

	@Autowired
	private IDocProjectService iDocProjectService;

	@Autowired
	private IDocCategoryService iDocCategoryService;

	@Override
	public boolean isCollected(User user, Long docCategoryId) {
		return this.count(Wrappers.<DocCategoryCollect>lambdaQuery().func(wrapper -> {
			wrapper.eq(DocCategoryCollect::getDocCategoryId, docCategoryId);
			wrapper.eq(DocCategoryCollect::getMemberUserId, user.getUserId());
			wrapper.eq(DocCategoryCollect::getDeleted, false);
		})) > 0;
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public Result<?> optCollect(int type, User user, String docProjectUuid, String docCategoryUuid) {
		DocProject docProject = iDocProjectService
				.getOne(Wrappers.<DocProject>lambdaQuery().eq(DocProject::getUuid, docProjectUuid));
		DocCategory docCategory = iDocCategoryService.getOne(Wrappers.<DocCategory>lambdaQuery().func(wrapper -> {
			wrapper.eq(DocCategory::getDocProjectId, docProject.getId());
			wrapper.eq(DocCategory::getUuid, docCategoryUuid);
		}));
		// 取消收藏
		this.update(Wrappers.<DocCategoryCollect>lambdaUpdate().func(wrapper -> {
			wrapper.set(DocCategoryCollect::getDeleted, true);
			wrapper.set(DocCategoryCollect::getUpdatedAt, new Date());
			wrapper.eq(DocCategoryCollect::getDeleted, false);
			wrapper.eq(DocCategoryCollect::getMemberUserId, user.getUserId());
			wrapper.in(DocCategoryCollect::getDocCategoryId, docCategory.getId());
		}));
		// 添加收藏
		if (type == 1) {
			DocCategoryCollect docCategoryCollect = new DocCategoryCollect();
			docCategoryCollect.setDocProjectId(docCategory.getDocProjectId());
			docCategoryCollect.setDocCategoryId(docCategory.getId());
			docCategoryCollect.setMemberUserId(user.getUserId());
			docCategoryCollect.setCreatedAt(new Date());
			docCategoryCollect.setUpdatedAt(docCategoryCollect.getCreatedAt());
			docCategoryCollect.setDeleted(false);
			this.save(docCategoryCollect);
		}
		return Result.success();
	}

	@Override
	public Page<DocCategoryCollectDto> findPage(Page<DocCategoryCollectDto> page, User user,
			DocCategoryCollectDto form) {
		return null;
	}
}
