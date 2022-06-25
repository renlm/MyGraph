package cn.renlm.graph.controller.qrtz;

import java.util.List;

import javax.annotation.Resource;

import org.quartz.CronExpression;
import org.quartz.JobDataMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.codec.Base64;
import cn.hutool.core.util.ClassUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.asymmetric.KeyType;
import cn.hutool.crypto.asymmetric.RSA;
import cn.hutool.json.JSONUtil;
import cn.renlm.graph.common.Role;
import cn.renlm.graph.modular.qrtz.JobBean;
import cn.renlm.graph.modular.qrtz.JobConfig;
import cn.renlm.graph.modular.qrtz.JobConfig.JobItem;
import cn.renlm.graph.modular.qrtz.dto.QrtzLogsDto;
import cn.renlm.graph.modular.qrtz.dto.QrtzTriggersDto;
import cn.renlm.graph.modular.qrtz.entity.QrtzLogs;
import cn.renlm.graph.modular.qrtz.service.IQrtzLogsService;
import cn.renlm.graph.modular.qrtz.service.IQrtzTriggersService;
import cn.renlm.graph.response.Datagrid;
import cn.renlm.graph.response.Result;
import cn.renlm.plugins.MyUtil.MyXStreamUtil;

/**
 * 定时任务
 * 
 * @author Renlm
 *
 */
@Controller
@RequestMapping("/qrtz")
public class QuartzController {

	private static final JobConfig config = MyXStreamUtil.read(JobConfig.class, "Jobs.xml");

	@Resource
	private RSA rsa;

	@Autowired
	private IQrtzLogsService iQrtzLogsService;

	@Autowired
	private IQrtzTriggersService iQrtzTriggersService;

	/**
	 * 任务管理
	 * 
	 * @param model
	 * @return
	 */
	@RequestMapping("/jobs")
	public String jobs(ModelMap model) {
		String actuator = rsa.encryptBase64("cn.renlm.graph.modular.qrtz.service.IQrtzLogsService.exportDataToFile",
				KeyType.PrivateKey);
		model.put("actuator", Base64.encodeUrlSafe(actuator));
		return "qrtz/jobs";
	}

	/**
	 * 任务清单
	 * 
	 * @return
	 */
	@ResponseBody
	@GetMapping("/ajax/repertoires")
	public List<JobItem> repertoires() {
		return config.getJobs();
	}

	/**
	 * 任务列表
	 * 
	 * @param page
	 * @param form
	 * @return
	 */
	@ResponseBody
	@GetMapping("/job/ajax/list")
	public Datagrid<QrtzTriggersDto> jobAjaxList(Page<QrtzTriggersDto> page, QrtzTriggersDto form) {
		Page<QrtzTriggersDto> pager = iQrtzTriggersService.findPage(page, form);
		return Datagrid.of(pager);
	}

	/**
	 * 恢复任务
	 * 
	 * @param triggerName
	 * @return
	 */
	@ResponseBody
	@PostMapping("/job/ajax/resume")
	@PreAuthorize(Role.AdminSpEL)
	public Result<?> jobAjaxResume(String triggerName) {
		try {
			iQrtzTriggersService.resume(triggerName);
			return Result.success();
		} catch (Exception e) {
			e.printStackTrace();
			return Result.error("出错了");
		}
	}

	/**
	 * 停止任务
	 * 
	 * @param triggerName
	 * @return
	 */
	@ResponseBody
	@PostMapping("/job/ajax/pause")
	@PreAuthorize(Role.AdminSpEL)
	public Result<?> jobAjaxPause(String triggerName) {
		try {
			iQrtzTriggersService.pause(triggerName);
			return Result.success();
		} catch (Exception e) {
			e.printStackTrace();
			return Result.error("出错了");
		}
	}

	/**
	 * 立即执行
	 * 
	 * @param triggerName
	 * @return
	 */
	@ResponseBody
	@PostMapping("/job/ajax/run")
	@PreAuthorize(Role.AdminSpEL)
	public Result<?> jobAjaxRun(String triggerName) {
		try {
			iQrtzTriggersService.run(triggerName);
			return Result.success();
		} catch (Exception e) {
			e.printStackTrace();
			return Result.error("出错了");
		}
	}

	/**
	 * 任务弹窗（新增|编辑）
	 * 
	 * @param model
	 * @param triggerName
	 * @return
	 */
	@GetMapping("/job/dialog")
	public String jobDialog(ModelMap model, String triggerName) {
		QrtzTriggersDto detail = new QrtzTriggersDto();
		if (StrUtil.isNotBlank(triggerName)) {
			QrtzTriggersDto trigger = iQrtzTriggersService.findDetail(triggerName);
			BeanUtil.copyProperties(trigger, detail);
		}
		model.put("trigger", detail);
		return "qrtz/jobDialog";
	}

	/**
	 * 保存任务（新建|编辑）
	 * 
	 * @param triggerName
	 * @param jobName
	 * @param jobClassName
	 * @param cronExpression
	 * @param description
	 * @param jobDataMapJson
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/job/ajax/save")
	@PreAuthorize(Role.AdminSpEL)
	public Result<?> jobAdd(String triggerName, String jobName, String jobClassName, String cronExpression,
			String description, String jobDataMapJson) {
		try {
			if (!CronExpression.isValidExpression(cronExpression)) {
				return Result.error("无效的时间表达式");
			}
			JobDataMap jobDataMap = new JobDataMap();
			if (JSONUtil.isTypeJSON(jobDataMapJson)) {
				jobDataMap.putAll(JSONUtil.toBean(jobDataMapJson, JobDataMap.class));
			}
			if (StrUtil.isNotBlank(triggerName)) {
				iQrtzTriggersService.update(triggerName, cronExpression, jobDataMap, description);
			} else {
				Class<JobBean> jobClass = ClassUtil.loadClass(jobClassName);
				if (iQrtzTriggersService.exists(jobClassName)) {
					return Result.error("任务重复");
				} else {
					iQrtzTriggersService.add(jobName, jobClass, cronExpression, jobDataMap, description);
				}
			}
			return Result.success();
		} catch (Exception e) {
			e.printStackTrace();
			return Result.error("出错了");
		}
	}

	/**
	 * 日志分页列表
	 * 
	 * @param page
	 * @param form
	 * @return
	 */
	@ResponseBody
	@GetMapping("/log/ajax/list")
	public Datagrid<QrtzLogs> logAjaxList(Page<QrtzLogs> page, QrtzLogsDto form) {
		Page<QrtzLogs> pager = iQrtzLogsService.findPage(page, form);
		return Datagrid.of(pager);
	}
}