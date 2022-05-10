package cn.renlm.graph.modular.qrtz.service.impl;

import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import cn.renlm.graph.modular.qrtz.entity.QrtzTriggers;
import cn.renlm.graph.modular.qrtz.mapper.QrtzTriggersMapper;
import cn.renlm.graph.modular.qrtz.service.IQrtzTriggersService;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author Renlm
 * @since 2022-05-10
 */
@Service
public class QrtzTriggersServiceImpl extends ServiceImpl<QrtzTriggersMapper, QrtzTriggers> implements IQrtzTriggersService {

}
