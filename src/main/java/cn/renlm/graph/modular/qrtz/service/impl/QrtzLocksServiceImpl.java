package cn.renlm.graph.modular.qrtz.service.impl;

import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import cn.renlm.graph.modular.qrtz.entity.QrtzLocks;
import cn.renlm.graph.modular.qrtz.mapper.QrtzLocksMapper;
import cn.renlm.graph.modular.qrtz.service.IQrtzLocksService;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author Renlm
 * @since 2022-05-10
 */
@Service
public class QrtzLocksServiceImpl extends ServiceImpl<QrtzLocksMapper, QrtzLocks> implements IQrtzLocksService {

}
