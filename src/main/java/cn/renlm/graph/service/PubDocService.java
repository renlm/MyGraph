package cn.renlm.graph.service;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.lang.tree.Tree;
import cn.hutool.core.util.NumberUtil;
import cn.renlm.graph.modular.doc.entity.DocCategory;
import cn.renlm.graph.modular.doc.entity.DocCategoryShare;
import cn.renlm.graph.modular.doc.entity.DocProject;
import cn.renlm.graph.modular.doc.service.IDocCategoryService;
import cn.renlm.graph.modular.doc.service.IDocCategoryShareService;
import cn.renlm.graph.modular.doc.service.IDocProjectService;

/**
 * 公共文档
 * 
 * @author Renlm
 *
 */
@Service
public class PubDocService {

	@Autowired
	private IDocProjectService iDocProjectService;

	@Autowired
	private IDocCategoryService iDocCategoryService;

	@Autowired
	private IDocCategoryShareService iDocCategoryShareService;

	/**
	 * 获取树形结构
	 * 
	 * @param shareUuid
	 * @return
	 */
	public List<Tree<Long>> getTree(String shareUuid) {
		DocCategoryShare docCategoryShare = iDocCategoryShareService
				.getOne(Wrappers.<DocCategoryShare>lambdaQuery().func(wrapper -> {
					wrapper.eq(DocCategoryShare::getUuid, shareUuid);
					wrapper.eq(DocCategoryShare::getDeleted, false);
					wrapper.eq(DocCategoryShare::getDisabled, false);
				}));
		if (docCategoryShare == null) {
			return CollUtil.newArrayList();
		} else if (!NumberUtil.equals(docCategoryShare.getEffectiveType(), -1)
				&& DateUtil.compare(new Date(), docCategoryShare.getDeadline(), DatePattern.NORM_DATE_PATTERN) > 0) {
			return CollUtil.newArrayList();
		}
		DocCategory docCategory = iDocCategoryService.getById(docCategoryShare.getDocCategoryId());
		DocProject docProject = iDocProjectService.getById(docCategoryShare.getDocProjectId());
		return iDocCategoryService.getTree(docProject.getUuid(), true, docCategory.getId());
	}
}