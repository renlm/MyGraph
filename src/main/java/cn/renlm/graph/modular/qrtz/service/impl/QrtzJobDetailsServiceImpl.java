package cn.renlm.graph.modular.qrtz.service.impl;

import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import cn.renlm.graph.modular.qrtz.entity.QrtzJobDetails;
import cn.renlm.graph.modular.qrtz.mapper.QrtzJobDetailsMapper;
import cn.renlm.graph.modular.qrtz.service.IQrtzJobDetailsService;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author Renlm
 * @since 2022-05-10
 */
@Service
public class QrtzJobDetailsServiceImpl extends ServiceImpl<QrtzJobDetailsMapper, QrtzJobDetails> implements IQrtzJobDetailsService {

}
