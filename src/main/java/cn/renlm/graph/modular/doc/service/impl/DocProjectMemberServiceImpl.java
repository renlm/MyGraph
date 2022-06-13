package cn.renlm.graph.modular.doc.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import cn.renlm.graph.dto.User;
import cn.renlm.graph.modular.doc.dto.DocProjectMemberDto;
import cn.renlm.graph.modular.doc.entity.DocProjectMember;
import cn.renlm.graph.modular.doc.mapper.DocProjectMemberMapper;
import cn.renlm.graph.modular.doc.service.IDocProjectMemberService;
import cn.renlm.graph.response.Result;

/**
 * <p>
 * 文档项目-成员 服务实现类
 * </p>
 *
 * @author Renlm
 * @since 2022-06-13
 */
@Service
public class DocProjectMemberServiceImpl extends ServiceImpl<DocProjectMemberMapper, DocProjectMember>
		implements IDocProjectMemberService {

	@Override
	public Page<DocProjectMemberDto> findAuthAccessPage(Page<DocProjectMemberDto> page, User user,
			DocProjectMemberDto form) {
		return this.baseMapper.findAuthAccessPage(page, user, form);
	}

	@Override
	public Result<?> addRoleMember(User user, Integer role, String docProjectUuid, List<String> userIds) {
		return null;
	}

	@Override
	public Result<?> removeRoleMember(User user, String docProjectUuid, List<String> userIds) {
		return null;
	}
}
