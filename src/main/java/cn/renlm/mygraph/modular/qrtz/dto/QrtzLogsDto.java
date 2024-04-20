package cn.renlm.mygraph.modular.qrtz.dto;

import cn.renlm.mygraph.modular.qrtz.entity.QrtzLogs;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 定时任务日志
 * 
 * @author RenLiMing(任黎明)
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