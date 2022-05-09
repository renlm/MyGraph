package cn.renlm.graph.modular.qrtz.service.impl;

import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import cn.renlm.graph.modular.qrtz.entity.QrtzLogs;
import cn.renlm.graph.modular.qrtz.mapper.QrtzLogsMapper;
import cn.renlm.graph.modular.qrtz.service.IQrtzLogsService;

/**
 * <p>
 * 定时任务日志 服务实现类
 * </p>
 *
 * @author Renlm
 * @since 2022-05-09
 */
@Service
public class QrtzLogsServiceImpl extends ServiceImpl<QrtzLogsMapper, QrtzLogs> implements IQrtzLogsService {

}
