package cn.renlm.graph.modular.qrtz.service.impl;

import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import cn.renlm.graph.modular.qrtz.entity.QrtzSchedulerState;
import cn.renlm.graph.modular.qrtz.mapper.QrtzSchedulerStateMapper;
import cn.renlm.graph.modular.qrtz.service.IQrtzSchedulerStateService;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author Renlm
 * @since 2022-05-10
 */
@Service
public class QrtzSchedulerStateServiceImpl extends ServiceImpl<QrtzSchedulerStateMapper, QrtzSchedulerState> implements IQrtzSchedulerStateService {

}
