package cn.renlm.mygraph.modular.sys.service.impl;

import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.renlm.mygraph.modular.sys.entity.SysLoginLog;
import cn.renlm.mygraph.modular.sys.mapper.SysLoginLogMapper;
import cn.renlm.mygraph.modular.sys.service.ISysLoginLogService;

/**
 * <p>
 * 系统登录日志 服务实现类
 * </p>
 *
 * @author RenLiMing(任黎明)
 * @since 2022-05-16
 */
@Service
public class SysLoginLogServiceImpl extends ServiceImpl<SysLoginLogMapper, SysLoginLog> implements ISysLoginLogService {

	@Override
	public Page<SysLoginLog> findPage(Page<SysLoginLog> page, SysLoginLog form) {
		return this.page(page, Wrappers.<SysLoginLog>lambdaQuery().func(wrapper -> {
			wrapper.orderByDesc(SysLoginLog::getLoginTime);
			wrapper.orderByDesc(SysLoginLog::getId);
			if (StrUtil.isNotBlank(form.getUserId())) {
				wrapper.eq(SysLoginLog::getUserId, form.getUserId());
			}
			if (ObjectUtil.isNotEmpty(form.getLoginTime())) {
				wrapper.ge(SysLoginLog::getLoginTime, form.getLoginTime());
			}
		}));
	}
}
