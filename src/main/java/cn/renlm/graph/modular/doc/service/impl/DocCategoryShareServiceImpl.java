package cn.renlm.graph.modular.doc.service.impl;

import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import cn.renlm.graph.dto.User;
import cn.renlm.graph.modular.doc.dto.DocCategoryShareDto;
import cn.renlm.graph.modular.doc.entity.DocCategoryShare;
import cn.renlm.graph.modular.doc.mapper.DocCategoryShareMapper;
import cn.renlm.graph.modular.doc.service.IDocCategoryShareService;
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

	@Override
	public Result<DocCategoryShareDto> ajaxSave(User user, DocCategoryShareDto form) {
		return null;
	}
}
