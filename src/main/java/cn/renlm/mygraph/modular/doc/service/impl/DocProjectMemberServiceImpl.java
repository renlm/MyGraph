package cn.renlm.mygraph.modular.doc.service.impl;

import static cn.hutool.core.text.StrPool.COMMA;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.lang.tree.Tree;
import cn.hutool.core.util.BooleanUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.renlm.mygraph.dto.User;
import cn.renlm.mygraph.modular.doc.dto.DocProjectMemberDto;
import cn.renlm.mygraph.modular.doc.entity.DocProject;
import cn.renlm.mygraph.modular.doc.entity.DocProjectMember;
import cn.renlm.mygraph.modular.doc.mapper.DocProjectMemberMapper;
import cn.renlm.mygraph.modular.doc.service.IDocProjectMemberService;
import cn.renlm.mygraph.modular.doc.service.IDocProjectService;
import cn.renlm.mygraph.modular.sys.entity.SysOrg;
import cn.renlm.mygraph.modular.sys.entity.SysUser;
import cn.renlm.mygraph.modular.sys.service.ISysOrgService;
import cn.renlm.mygraph.modular.sys.service.ISysUserService;
import cn.renlm.plugins.MyResponse.Result;
import cn.renlm.plugins.MyResponse.StatusCode;
import cn.renlm.plugins.MyUtil.MyTreeUtil;

/**
 * <p>
 * 文档项目-成员 服务实现类
 * </p>
 *
 * @author RenLiMing(任黎明)
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
			} else if (BooleanUtil.isFalse(form.getAccessAuth())) {
				wrapper.notInSql(SysUser::getUserId, accessAuthSql);
			}
			// 组织机构过滤
			if (StrUtil.isNotBlank(form.getOrgIds())) {
				List<Long> allSysOrgIds = CollUtil.newArrayList();
				List<SysOrg> orgs = iSysOrgService.list(Wrappers.<SysOrg>lambdaQuery().in(SysOrg::getOrgId,
						StrUtil.splitTrim(form.getOrgIds(), COMMA)));
				if (CollUtil.isNotEmpty(orgs)) {
					List<Long> sysOrgIds = orgs.stream().map(SysOrg::getId).collect(Collectors.toList());
					sysOrgIds.forEach(it -> {
						List<Tree<Long>> list = MyTreeUtil.getAllNodes(iSysOrgService.getTree(true, it));
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
		Map<String, DocProjectMemberDto> map = new LinkedHashMap<>();
		List<DocProjectMemberDto> list = pager.getRecords().stream().filter(Objects::nonNull).map(obj -> {
			DocProjectMemberDto data = BeanUtil.copyProperties(obj, DocProjectMemberDto.class);
			data.setDocProjectId(docProject.getId());
			data.setDocProjectUuid(docProject.getUuid());
			data.setMemberUserId(obj.getUserId());
			map.put(data.getMemberUserId(), data);
			return data;
		}).collect(Collectors.toList());
		// 获取文档项目授权角色
		if (CollUtil.isNotEmpty(list)) {
			List<DocProjectMember> members = this.list(Wrappers.<DocProjectMember>lambdaQuery().func(wrapper -> {
				wrapper.eq(DocProjectMember::getDocProjectId, docProject.getId());
				wrapper.eq(DocProjectMember::getDeleted, false);
			}));
			members.forEach(member -> {
				DocProjectMemberDto item = map.get(member.getMemberUserId());
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
	@Transactional(rollbackFor = Exception.class)
	public Result<?> addRoleMember(boolean verify, User user, Integer role, String docProjectUuid,
			List<String> userIds) {
		DocProject docProject = iDocProjectService
				.getOne(Wrappers.<DocProject>lambdaQuery().eq(DocProject::getUuid, docProjectUuid));
		if (verify) {
			long members = this.count(Wrappers.<DocProjectMember>lambdaQuery().func(wrapper -> {
				wrapper.eq(DocProjectMember::getRole, 3);
				wrapper.eq(DocProjectMember::getDocProjectId, docProject.getId());
				wrapper.eq(DocProjectMember::getMemberUserId, user.getUserId());
				wrapper.eq(DocProjectMember::getDeleted, false);
			}));
			if (members == 0) {
				return Result.of(StatusCode.FORBIDDEN, "非管理员，无操作权限");
			}
		}
		// 添加新关联关系
		if (CollUtil.isNotEmpty(userIds)) {
			List<DocProjectMember> list = CollUtil.newArrayList();
			userIds.forEach(userId -> {
				DocProjectMember data = new DocProjectMember();
				data.setDocProjectId(docProject.getId());
				data.setMemberUserId(userId);
				data.setRole(role);
				data.setCreatedAt(new Date());
				data.setDeleted(false);
				list.add(data);
			});
			if (CollUtil.isNotEmpty(list)) {
				this.saveBatch(list);
			}
		}
		return Result.success();
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public Result<?> editRole(User user, Integer role, String docProjectUuid, List<String> userIds) {
		Result<?> result1 = this.removeRoleMember(user, docProjectUuid, userIds);
		if (result1.isSuccess()) {
			return this.addRoleMember(false, user, role, docProjectUuid, userIds);
		} else {
			return result1;
		}
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public Result<?> removeRoleMember(User user, String docProjectUuid, List<String> userIds) {
		DocProject docProject = iDocProjectService
				.getOne(Wrappers.<DocProject>lambdaQuery().eq(DocProject::getUuid, docProjectUuid));
		long members = this.count(Wrappers.<DocProjectMember>lambdaQuery().func(wrapper -> {
			wrapper.eq(DocProjectMember::getRole, 3);
			wrapper.eq(DocProjectMember::getDocProjectId, docProject.getId());
			wrapper.eq(DocProjectMember::getMemberUserId, user.getUserId());
			wrapper.eq(DocProjectMember::getDeleted, false);
		}));
		if (members == 0) {
			return Result.of(StatusCode.FORBIDDEN, "非管理员，无操作权限");
		}
		if (CollUtil.isNotEmpty(userIds)) {
			this.update(Wrappers.<DocProjectMember>lambdaUpdate().func(wrapper -> {
				wrapper.set(DocProjectMember::getDeleted, true);
				wrapper.set(DocProjectMember::getUpdatedAt, new Date());
				wrapper.eq(DocProjectMember::getDocProjectId, docProject.getId());
				wrapper.in(DocProjectMember::getMemberUserId, userIds);
				wrapper.eq(DocProjectMember::getDeleted, false);
			}));
		}
		return Result.success();
	}
}
