package cn.renlm.graph.controller.qrtz;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import cn.hutool.core.codec.Base64;
import cn.hutool.crypto.asymmetric.KeyType;
import cn.hutool.crypto.asymmetric.RSA;
import cn.renlm.graph.modular.qrtz.JobConfig;
import cn.renlm.graph.modular.qrtz.JobConfig.JobItem;
import cn.renlm.graph.modular.qrtz.dto.QrtzLogsDto;
import cn.renlm.graph.modular.qrtz.entity.QrtzLogs;
import cn.renlm.graph.modular.qrtz.service.IQrtzLogsService;
import cn.renlm.graph.response.Datagrid;
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