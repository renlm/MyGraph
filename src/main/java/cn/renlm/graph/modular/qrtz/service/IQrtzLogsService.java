package cn.renlm.graph.modular.qrtz.service;

import org.quartz.JobExecutionContext;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

import cn.hutool.log.level.Level;
import cn.renlm.graph.modular.qrtz.dto.QrtzLogsDto;
import cn.renlm.graph.modular.qrtz.entity.QrtzLogs;
import cn.renlm.graph.modular.sys.entity.SysFile;

/**
 * <p>
 * 定时任务日志 服务类
 * </p>
 *
 * @author RenLiMing(任黎明)
 * @since 2022-05-09
 */
public interface IQrtzLogsService extends IService<QrtzLogs> {

	/**
	 * 插入日志
	 * 
	 * @param context
	 * @param batch
	 * @param seq
	 * @param level
	 * @param text
	 */
	void insert(final JobExecutionContext context, String batch, int seq, Level level, String text);

	/**
	 * 分页列表
	 * 
	 * @param page
	 * @param form
	 * @return
	 */
	Page<QrtzLogs> findPage(Page<QrtzLogs> page, QrtzLogsDto form);

	/**
	 * 导出文件
	 * 
	 * @param file
	 */
	void exportDataToFile(SysFile file);

}
