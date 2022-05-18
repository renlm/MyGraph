package cn.renlm.graph.jobs;

import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import org.quartz.JobExecutionContext;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;

import cn.hutool.log.level.Level;
import cn.renlm.graph.modular.qrtz.JobBean;
import cn.renlm.graph.modular.qrtz.service.IQrtzLogsService;
import cn.renlm.graph.modular.sys.entity.SysUser;
import cn.renlm.graph.modular.sys.service.ISysUserService;

/**
 * 演示账号重置密码（0 0/5 * * * ?）
 * 
 * @author Renlm
 *
 */
@Component
public class DemoResetPasswdJob extends JobBean {

	private ISysUserService iSysUserService;

	public DemoResetPasswdJob(IQrtzLogsService iQrtzLogsService) {
		super(iQrtzLogsService);
	}

	@Override
	public void exec(String batch, AtomicInteger seq, Map<String, Object> params) {
		JobExecutionContext context = (JobExecutionContext) params.get(CONTEXT_KEY);
		String username = "S-renyy";
		String password = "Aac^123654.";
		iSysUserService.update(Wrappers.<SysUser>lambdaUpdate().func(wrapper -> {
			wrapper.set(SysUser::getPassword, new BCryptPasswordEncoder().encode(password));
			wrapper.eq(SysUser::getUsername, username);
		}));
		this.log(context, batch, seq, Level.INFO, username + "，重置密码为：" + password);
	}
}