package cn.renlm.graph.service;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.lang.tree.Tree;
import cn.hutool.core.util.BooleanUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.StrUtil;
import cn.renlm.graph.dto.DocShareUser;
import cn.renlm.graph.modular.doc.dto.DocCategoryShareDto;
import cn.renlm.graph.modular.doc.entity.DocCategory;
import cn.renlm.graph.modular.doc.entity.DocCategoryShare;
import cn.renlm.graph.modular.doc.entity.DocProject;
import cn.renlm.graph.modular.doc.service.IDocCategoryService;
import cn.renlm.graph.modular.doc.service.IDocCategoryShareService;
import cn.renlm.graph.modular.doc.service.IDocProjectService;
import jakarta.servlet.http.HttpServletRequest;

/**
 * 公共文档
 * 
 * @author RenLiMing(任黎明)
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
	 * 获取分享信息
	 * 
	 * @param shareUuid
	 * @return
	 */
	public DocCategoryShareDto getDocCategoryShare(String shareUuid) {
		DocCategoryShare docCategoryShare = iDocCategoryShareService
				.getOne(Wrappers.<DocCategoryShare>lambdaQuery().func(wrapper -> {
					wrapper.eq(DocCategoryShare::getUuid, shareUuid);
				}));
		if (docCategoryShare == null) {
			return null;
		}
		DocCategoryShareDto dto = BeanUtil.copyProperties(docCategoryShare, DocCategoryShareDto.class);
		DocCategory docCategory = iDocCategoryService.getById(docCategoryShare.getDocCategoryId());
		DocProject docProject = iDocProjectService.getById(docCategoryShare.getDocProjectId());
		dto.setDocCategoryId(docCategory.getId());
		dto.setDocCategoryUuid(docCategory.getUuid());
		dto.setDocCategoryName(docCategory.getText());
		dto.setParentsCategorName(docCategory.getFullname());
		dto.setDocProjectId(docProject.getId());
		dto.setDocProjectUuid(docProject.getUuid());
		dto.setDocProjectName(docProject.getProjectName());
		if (!BooleanUtil.isFalse(dto.getDisabled()) || !BooleanUtil.isFalse(dto.getDeleted())
				|| !BooleanUtil.isFalse(docCategory.getDeleted()) || !BooleanUtil.isFalse(docProject.getDeleted())) {
			dto.setStatus(2);
		} else if (!NumberUtil.equals(dto.getEffectiveType(), -1)
				&& DateUtil.compare(new Date(), dto.getDeadline(), DatePattern.NORM_DATE_PATTERN) > 0) {
			dto.setStatus(3);
		} else {
			dto.setStatus(1);
		}
		return dto;
	}

	/**
	 * 获取树形结构
	 * 
	 * @param request
	 * @param shareUuid
	 * @return
	 */
	public List<Tree<Long>> getTree(HttpServletRequest request, String shareUuid) {
		DocCategoryShareDto docCategoryShare = this.getDocCategoryShare(shareUuid);
		if (docCategoryShare == null || !NumberUtil.equals(docCategoryShare.getStatus(), 1)) {
			return CollUtil.newArrayList();
		}
		if (NumberUtil.equals(docCategoryShare.getShareType(), 2)) {
			DocShareUser user = DocShareUser.getInfo(request, shareUuid);
			if (user == null || !StrUtil.equals(shareUuid, user.getShareUuid())) {
				return CollUtil.newArrayList();
			}
		}
		String docProjectUuid = docCategoryShare.getDocProjectUuid();
		Long docCategoryId = docCategoryShare.getDocCategoryId();
		return iDocCategoryService.getTree(docProjectUuid, true, docCategoryId);
	}

}
