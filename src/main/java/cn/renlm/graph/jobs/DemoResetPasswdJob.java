package cn.renlm.graph.jobs;

import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import org.quartz.JobExecutionContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.json.JSONUtil;
import cn.hutool.log.level.Level;
import cn.renlm.graph.modular.qrtz.JobBean;
import cn.renlm.graph.modular.qrtz.service.IQrtzLogsService;
import cn.renlm.graph.modular.sys.entity.SysUser;
import cn.renlm.graph.modular.sys.service.ISysUserService;

/**
 * 演示账号重置密码（0 0/5 * * * ?）
 * 
 * @author RenLiMing(任黎明)
 *
 */
@Component
public class DemoResetPasswdJob extends JobBean {

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Autowired
	private ISysUserService iSysUserService;

	public DemoResetPasswdJob(IQrtzLogsService iQrtzLogsService) {
		super(iQrtzLogsService);
	}

	@Override
	public void exec(String batch, AtomicInteger seq, Map<String, Object> params) {
		JobExecutionContext context = (JobExecutionContext) params.get(CONTEXT_KEY);
		List<String> usernames = CollUtil.newArrayList("S-linghc", "S-renyy");
		String password = "123654";
		iSysUserService.update(Wrappers.<SysUser>lambdaUpdate().func(wrapper -> {
			wrapper.set(SysUser::getPassword, passwordEncoder.encode(password));
			wrapper.in(SysUser::getUsername, usernames);
		}));
		this.log(context, batch, seq, Level.INFO, JSONUtil.toJsonStr(usernames) + "，重置密码为：" + password);
	}

}
