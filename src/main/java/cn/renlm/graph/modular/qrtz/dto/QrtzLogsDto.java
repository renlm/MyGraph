package cn.renlm.graph.modular.qrtz.dto;

import cn.renlm.graph.modular.qrtz.entity.QrtzLogs;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 定时任务日志
 * 
 * @author Renlm
 *
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class QrtzLogsDto extends QrtzLogs {

	private static final long serialVersionUID = 1L;

	/**
	 * 排序方式（ASC、DESC）
	 */
	private String order;

}