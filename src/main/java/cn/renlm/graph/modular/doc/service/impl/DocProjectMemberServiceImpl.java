package cn.renlm.graph.modular.doc.service.impl;

import static cn.hutool.core.text.StrPool.COMMA;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.lang.tree.Tree;
import cn.hutool.core.util.BooleanUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.renlm.graph.dto.User;
import cn.renlm.graph.modular.doc.dto.DocProjectMemberDto;
import cn.renlm.graph.modular.doc.entity.DocProject;
import cn.renlm.graph.modular.doc.entity.DocProjectMember;
import cn.renlm.graph.modular.doc.mapper.DocProjectMemberMapper;
import cn.renlm.graph.modular.doc.service.IDocProjectMemberService;
import cn.renlm.graph.modular.doc.service.IDocProjectService;
import cn.renlm.graph.modular.sys.entity.SysOrg;
import cn.renlm.graph.modular.sys.entity.SysUser;
import cn.renlm.graph.modular.sys.service.ISysOrgService;
import cn.renlm.graph.modular.sys.service.ISysUserService;
import cn.renlm.graph.response.Result;
import cn.renlm.graph.util.TreeExtraUtil;

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

	@Autowired
	private ISysOrgService iSysOrgService;

	@Autowired
	private ISysUserService iSysUserService;

	@Autowired
	private IDocProjectService iDocProjectService;

	@Override
	public Page<DocProjectMemberDto> findAuthAccessPage(Page<SysUser> page, User user, DocProjectMemberDto form) {
		Page<DocProjectMemberDto> result = new Page<>(page.getCurrent(), page.getSize());
		BeanUtil.copyProperties(page, result);
		if (StrUtil.isBlank(form.getDocProjectUuid())) {
			return result;
		}
		// 文档项目
		DocProject docProject = iDocProjectService
				.getOne(Wrappers.<DocProject>lambdaQuery().eq(DocProject::getUuid, form.getDocProjectUuid()));
		if (ObjectUtil.isEmpty(docProject)) {
			return result;
		}
		// 人员分页
		Page<SysUser> pager = iSysUserService.page(page, Wrappers.<SysUser>lambdaQuery().func(wrapper -> {
			// 授权筛选
			String accessAuthSql = StrUtil.indexedFormat(
					"select dpm.member_user_id from doc_project dp, doc_project_member dpm where dp.id = {0} and dp.id = dpm.doc_project_id and dpm.deleted = 0 and dpm.`role` in (1, 2, 3)",
					docProject.getId().toString());
			if (BooleanUtil.isTrue(form.getAccessAuth())) {
				wrapper.inSql(SysUser::getUserId, accessAuthSql);
			} else {
				wrapper.notIn(SysUser::getUserId, accessAuthSql);
			}
			// 组织机构过滤
			if (StrUtil.isNotBlank(form.getOrgIds())) {
				List<Long> allSysOrgIds = CollUtil.newArrayList();
				List<SysOrg> orgs = iSysOrgService.list(Wrappers.<SysOrg>lambdaQuery().in(SysOrg::getOrgId,
						StrUtil.splitTrim(form.getOrgIds(), COMMA)));
				if (CollUtil.isNotEmpty(orgs)) {
					List<Long> sysOrgIds = orgs.stream().map(SysOrg::getId).collect(Collectors.toList());
					sysOrgIds.forEach(it -> {
						List<Tree<Long>> list = TreeExtraUtil.getAllNodes(iSysOrgService.getTree(true, it));
						CollUtil.addAll(allSysOrgIds, list.stream().map(Tree::getId).collect(Collectors.toList()));
					});
					if (CollUtil.isNotEmpty(allSysOrgIds)) {
						wrapper.inSql(SysUser::getId, StrUtil.indexedFormat(
								"select suo.sys_user_id from sys_user_org suo, sys_org so where suo.deleted = 0 and so.deleted = 0 and suo.sys_org_id = so.id and so.id in ({0})",
								StrUtil.join(COMMA, allSysOrgIds)));
					}
				}
			}
			// 关键词过滤
			if (StrUtil.isNotBlank(form.getKeywords())) {
				wrapper.and(it -> {
					it.or().like(SysUser::getUsername, form.getKeywords());
					it.or().like(SysUser::getNickname, form.getKeywords());
					it.or().like(SysUser::getMobile, form.getKeywords());
					it.or().like(SysUser::getEmail, form.getKeywords());
				});
			}
			wrapper.orderByDesc(SysUser::getId);
		}));
		// 数据集
		Map<Long, DocProjectMemberDto> map = new LinkedHashMap<>();
		List<DocProjectMemberDto> list = pager.getRecords().stream().filter(Objects::nonNull).map(obj -> {
			DocProjectMemberDto data = BeanUtil.copyProperties(obj, DocProjectMemberDto.class);
			data.setDocProjectId(docProject.getId());
			data.setDocProjectUuid(docProject.getUuid());
			map.put(data.getDocProjectId(), data);
			return data;
		}).collect(Collectors.toList());
		// 获取文档项目授权角色
		if (CollUtil.isNotEmpty(list)) {
			List<DocProjectMember> members = this.list(Wrappers.<DocProjectMember>lambdaQuery().func(wrapper -> {
				wrapper.in(DocProjectMember::getDocProjectId, map.keySet());
				wrapper.eq(DocProjectMember::getMemberUserId, user.getUserId());
				wrapper.eq(DocProjectMember::getDeleted, false);
			}));
			members.forEach(member -> {
				DocProjectMemberDto item = map.get(member.getDocProjectId());
				if (ObjectUtil.isNotEmpty(item)) {
					if (item.getRole() == null) {
						item.setRole(member.getRole());
					} else {
						item.setRole(Math.max(item.getRole(), member.getRole()));
					}
				}
			});
			list.forEach(item -> {
				item.setAccessAuth(item.getRole() != null);
			});
		}
		// 封装结果
		return result.setRecords(list);
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
