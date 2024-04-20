package cn.renlm.mygraph.modular.qrtz.dto;

import java.io.Serializable;

import org.quartz.JobDataMap;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONUtil;
import lombok.Data;

/**
 * 
 * 触发器
 * 
 * @author RenLiMing(任黎明)
 *
 */
@Data
public class QrtzTriggersDto implements Serializable {

	private static final long serialVersionUID = 1L;

	private String schedName;

	private String jobGroup;

	private String jobName;

	private String jobClassName;

	private String triggerGroup;

	private String triggerName;

	private String timeZoneId;

	private String cronExpression;

	private String triggerState;

	private String description;

	private byte[] jobData;

	private JobDataMap jobDataMap;

	private String jobDataMapJson;

	public JobDataMap getJobDataMap() {
		if (this.jobData != null && this.jobData.length > 0) {
			this.jobDataMap = ObjectUtil.deserialize(this.jobData);
		}
		return this.jobDataMap;
	}

	public String getJobDataMapJson() {
		JobDataMap jobDataMap = getJobDataMap();
		if (jobDataMap != null) {
			this.jobDataMapJson = JSONUtil.toJsonPrettyStr(jobDataMap);
		}
		return this.jobDataMapJson;
	}
}