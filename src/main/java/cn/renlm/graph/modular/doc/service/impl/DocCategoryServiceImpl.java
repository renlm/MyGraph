package cn.renlm.graph.modular.doc.service.impl;

import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import cn.renlm.graph.modular.doc.entity.DocCategory;
import cn.renlm.graph.modular.doc.mapper.DocCategoryMapper;
import cn.renlm.graph.modular.doc.service.IDocCategoryService;

/**
 * <p>
 * 文档分类 服务实现类
 * </p>
 *
 * @author Renlm
 * @since 2022-06-13
 */
@Service
public class DocCategoryServiceImpl extends ServiceImpl<DocCategoryMapper, DocCategory> implements IDocCategoryService {

}
