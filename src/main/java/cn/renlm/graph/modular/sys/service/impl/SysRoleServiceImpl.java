package cn.renlm.graph.modular.sys.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import cn.hutool.core.util.StrUtil;
import cn.renlm.graph.modular.sys.entity.SysRole;
import cn.renlm.graph.modular.sys.mapper.SysRoleMapper;
import cn.renlm.graph.modular.sys.service.ISysRoleService;

/**
 * <p>
 * 角色 服务实现类
 * </p>
 *
 * @author Renlm
 * @since 2022-04-29
 */
@Service
public class SysRoleServiceImpl extends ServiceImpl<SysRoleMapper, SysRole> implements ISysRoleService {

	@Override
	public List<SysRole> findList(String userId) {
		return this.list(Wrappers.<SysRole>lambdaQuery().func(wrapper -> {
			wrapper.eq(SysRole::getDeleted, false);
			wrapper.eq(SysRole::getDisabled, false);
			wrapper.inSql(SysRole::getId, StrUtil.indexedFormat(
					"select sur.sys_role_id from sys_user su, sys_user_role sur where su.user_id = ''{0}'' and su.id = sur.sys_user_id and sur.deleted = 0",
					userId));
			wrapper.orderByAsc(SysRole::getLevel);
			wrapper.orderByAsc(SysRole::getSort);
			wrapper.orderByAsc(SysRole::getId);
		}));
	}
}
