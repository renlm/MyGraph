package cn.renlm.graph.modular.sys.service.impl;

import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import cn.renlm.graph.modular.sys.entity.SysLoginLog;
import cn.renlm.graph.modular.sys.mapper.SysLoginLogMapper;
import cn.renlm.graph.modular.sys.service.ISysLoginLogService;

/**
 * <p>
 * 系统登录日志 服务实现类
 * </p>
 *
 * @author Renlm
 * @since 2022-05-16
 */
@Service
public class SysLoginLogServiceImpl extends ServiceImpl<SysLoginLogMapper, SysLoginLog> implements ISysLoginLogService {

}
