package cn.renlm.graph.modular.doc.service.impl;

import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import cn.renlm.graph.modular.doc.entity.DocProjectMember;
import cn.renlm.graph.modular.doc.mapper.DocProjectMemberMapper;
import cn.renlm.graph.modular.doc.service.IDocProjectMemberService;

/**
 * <p>
 * 文档项目-成员 服务实现类
 * </p>
 *
 * @author Renlm
 * @since 2022-06-13
 */
@Service
public class DocProjectMemberServiceImpl extends ServiceImpl<DocProjectMemberMapper, DocProjectMember> implements IDocProjectMemberService {

}
