package cn.renlm.graph.modular.qrtz.service.impl;

import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import cn.renlm.graph.modular.qrtz.entity.QrtzFiredTriggers;
import cn.renlm.graph.modular.qrtz.mapper.QrtzFiredTriggersMapper;
import cn.renlm.graph.modular.qrtz.service.IQrtzFiredTriggersService;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author Renlm
 * @since 2022-05-10
 */
@Service
public class QrtzFiredTriggersServiceImpl extends ServiceImpl<QrtzFiredTriggersMapper, QrtzFiredTriggers> implements IQrtzFiredTriggersService {

}
