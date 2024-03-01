package cn.renlm.graph.modular.qrtz.service.impl;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Date;

import org.apache.poi.ss.usermodel.Workbook;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.Trigger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.core.enums.SqlKeyword;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import cn.hutool.core.exceptions.ExceptionUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import cn.hutool.log.level.Level;
import cn.hutool.system.HostInfo;
import cn.renlm.graph.modular.qrtz.dto.QrtzLogsDto;
import cn.renlm.graph.modular.qrtz.entity.QrtzLogs;
import cn.renlm.graph.modular.qrtz.mapper.QrtzLogsMapper;
import cn.renlm.graph.modular.qrtz.service.IQrtzLogsService;
import cn.renlm.graph.modular.sys.entity.SysFile;
import cn.renlm.graph.modular.sys.service.ISysFileService;
import cn.renlm.plugins.MyExcelUtil;

/**
 * <p>
 * 定时任务日志 服务实现类
 * </p>
 *
 * @author RenLiMing(任黎明)
 * @since 2022-05-09
 */
@Service
public class QrtzLogsServiceImpl extends ServiceImpl<QrtzLogsMapper, QrtzLogs> implements IQrtzLogsService {

	@Autowired
	private ISysFileService iSysFileService;

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void insert(final JobExecutionContext context, String batch, int seq, Level level, String text) {
		final JobDetail jobDetail = context.getJobDetail();
		final Trigger triger = context.getTrigger();
		QrtzLogs entity = new QrtzLogs();
		entity.setMachine(new HostInfo().getAddress());
		entity.setTriggerName(triger.getKey().getName());
		entity.setJobName(jobDetail.getKey().getName());
		entity.setJobClassName(jobDetail.getJobClass().getName());
		entity.setJobDataMapJson(JSONUtil.toJsonStr(triger.getJobDataMap()));
		entity.setBatch(batch);
		entity.setSeq(seq);
		entity.setLevel(level.name());
		entity.setText(text);
		entity.setCreatedAt(new Date());
		baseMapper.insert(entity);
	}

	@Override
	public Page<QrtzLogs> findPage(Page<QrtzLogs> page, QrtzLogsDto form) {
		return this.page(page, Wrappers.<QrtzLogs>lambdaQuery().func(wrapper -> {
			if (StrUtil.isNotBlank(form.getTriggerName())) {
				wrapper.eq(QrtzLogs::getTriggerName, form.getTriggerName());
			}
			if (StrUtil.isNotBlank(form.getBatch())) {
				wrapper.eq(QrtzLogs::getBatch, form.getBatch());
			}
			if (StrUtil.isNotBlank(form.getJobName())) {
				wrapper.eq(QrtzLogs::getJobName, form.getJobName());
			}
			if (StrUtil.isNotBlank(form.getJobClassName())) {
				wrapper.eq(QrtzLogs::getJobClassName, form.getJobClassName());
			}
			if (StrUtil.isNotBlank(form.getLevel())) {
				wrapper.eq(QrtzLogs::getLevel, form.getLevel());
			}
			if (StrUtil.isNotBlank(form.getOrder())
					&& SqlKeyword.ASC.getSqlSegment().toLowerCase().equals(form.getOrder())) {
				wrapper.orderByAsc(QrtzLogs::getCreatedAt).orderByAsc(QrtzLogs::getSeq);
			} else {
				wrapper.orderByDesc(QrtzLogs::getCreatedAt).orderByDesc(QrtzLogs::getSeq);
			}
		}));
	}

	@Override
	public void exportDataToFile(SysFile file) {
		QrtzLogsDto form = JSONUtil.toBean(file.getParamJson(), QrtzLogsDto.class);
		try (Workbook workbook = MyExcelUtil.createWorkbook("excel/qrtz/LogQuartz.excel.xml", false, sheet -> {
			Page<QrtzLogs> pager = new Page<>(1, 1000);
			pager = this.findPage(pager, form);
			sheet.write(pager.getRecords());
			while (pager.getCurrent() <= pager.getPages()) {
				pager.setCurrent(pager.getCurrent() + 1);
				pager = this.findPage(pager, form);
				sheet.write(pager.getRecords());
			}
		})) {
			ByteArrayOutputStream stream = new ByteArrayOutputStream();
			workbook.write(stream);
			file.setFileContent(stream.toByteArray());
			file.setSize((long) file.getFileContent().length);
			file.setStatus(5);
			file.setUpdatedAt(new Date());
		} catch (IOException e) {
			e.printStackTrace();
			file.setStatus(4);
			file.setUpdatedAt(new Date());
			file.setMessage(ExceptionUtil.stacktraceToString(e));
		} finally {
			iSysFileService.updateById(file);
		}
	}
}
