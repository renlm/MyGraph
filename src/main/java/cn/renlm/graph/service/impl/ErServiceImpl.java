package cn.renlm.graph.service.impl;

import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import cn.renlm.graph.entity.Er;
import cn.renlm.graph.mapper.ErMapper;
import cn.renlm.graph.service.IErService;

/**
 * <p>
 * ER模型 服务实现类
 * </p>
 *
 * @author Renlm
 * @since 2022-03-04
 */
@Service
public class ErServiceImpl extends ServiceImpl<ErMapper, Er> implements IErService {

}
