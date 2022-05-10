package cn.renlm.graph.modular.qrtz.service.impl;

import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import cn.renlm.graph.modular.qrtz.entity.QrtzCronTriggers;
import cn.renlm.graph.modular.qrtz.mapper.QrtzCronTriggersMapper;
import cn.renlm.graph.modular.qrtz.service.IQrtzCronTriggersService;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author Renlm
 * @since 2022-05-10
 */
@Service
public class QrtzCronTriggersServiceImpl extends ServiceImpl<QrtzCronTriggersMapper, QrtzCronTriggers> implements IQrtzCronTriggersService {

}
