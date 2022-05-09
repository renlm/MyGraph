package cn.renlm.graph.controller.qrtz;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import cn.hutool.core.codec.Base64;
import cn.hutool.crypto.asymmetric.KeyType;
import cn.hutool.crypto.asymmetric.RSA;
import cn.renlm.graph.modular.qrtz.JobConfig;
import cn.renlm.graph.modular.qrtz.JobConfig.JobItem;
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
}