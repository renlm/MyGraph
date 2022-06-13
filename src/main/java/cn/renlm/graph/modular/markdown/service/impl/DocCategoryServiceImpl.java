package cn.renlm.graph.modular.markdown.service.impl;

import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import cn.renlm.graph.modular.markdown.entity.DocCategory;
import cn.renlm.graph.modular.markdown.mapper.DocCategoryMapper;
import cn.renlm.graph.modular.markdown.service.IDocCategoryService;

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
