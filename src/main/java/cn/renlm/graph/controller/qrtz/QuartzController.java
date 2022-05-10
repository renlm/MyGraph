package cn.renlm.graph.controller.qrtz;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.codec.Base64;
import cn.hutool.core.util.ClassUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.asymmetric.KeyType;
import cn.hutool.crypto.asymmetric.RSA;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
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

	private static final JobConfig config = MyXStreamUtil.read(JobConfig.class, "scheduled/Jobs.xml");

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
	@RequestMapping("/ajax/repertoires")
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
	@RequestMapping("/job/ajax/list")
	public Page<QrtzTriggersDto> jobAjaxList(Page<QrtzTriggersDto> page, QrtzTriggersDto form) {
		return iQrtzTriggersService.findPage(page, form);
	}

	/**
	 * 恢复任务
	 * 
	 * @param triggerName
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/job/ajax/resume")
	public Result<?> jobAjaxResume(String triggerName) {
		try {
			iQrtzTriggersService.resume(triggerName);
			return Result.success();
		} catch (Exception e) {
			e.printStackTrace();
			return Result.error();
		}
	}

	/**
	 * 停止任务
	 * 
	 * @param triggerName
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/job/ajax/pause")
	public Result<?> jobAjaxPause(String triggerName) {
		try {
			iQrtzTriggersService.pause(triggerName);
			return Result.success();
		} catch (Exception e) {
			e.printStackTrace();
			return Result.error();
		}
	}

	/**
	 * 立即执行
	 * 
	 * @param triggerName
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/job/ajax/run")
	public Result<?> jobAjaxRun(String triggerName) {
		try {
			iQrtzTriggersService.run(triggerName);
			return Result.success();
		} catch (Exception e) {
			e.printStackTrace();
			return Result.error();
		}
	}

	/**
	 * 任务弹窗
	 * 
	 * @return
	 */
	@RequestMapping("/job/dialog")
	public String jobDialog() {
		return "qrtz/jobDialog";
	}

	/**
	 * 任务详情
	 * 
	 * @param triggerName
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/job/ajax/detail")
	public QrtzTriggersDto jobAjaxDetail(String triggerName) {
		return iQrtzTriggersService.findDetail(triggerName);
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
	public Result<?> jobAdd(String triggerName, String jobName, String jobClassName, String cronExpression,
			String description, String jobDataMapJson) {
		try {
			Map<String, Object> params = new LinkedHashMap<>();
			if (JSONUtil.isTypeJSON(jobDataMapJson)) {
				JSONObject jobDataMap = JSONUtil.parseObj(jobDataMapJson);
				BeanUtil.copyProperties(jobDataMap, params);
			}
			Class<JobBean> jobClass = ClassUtil.loadClass(jobClassName);
			if (StrUtil.isNotBlank(triggerName)) {
				iQrtzTriggersService.update(triggerName, cronExpression, params, description);
			} else {
				if (iQrtzTriggersService.exists(jobClassName)) {
					return Result.error("任务重复");
				} else {
					iQrtzTriggersService.add(jobName, jobClass, cronExpression, params, description);
				}
			}
			return Result.success();
		} catch (Exception e) {
			e.printStackTrace();
			return Result.error();
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
	@RequestMapping("/log/ajax/list")
	public Datagrid<QrtzLogs> logAjaxList(Page<QrtzLogs> page, QrtzLogsDto form) {
		Page<QrtzLogs> pager = iQrtzLogsService.findPage(page, form);
		return Datagrid.of(pager);
	}
}