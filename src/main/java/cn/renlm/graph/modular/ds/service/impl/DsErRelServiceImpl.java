package cn.renlm.graph.modular.ds.service.impl;

import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import cn.renlm.graph.modular.ds.entity.DsErRel;
import cn.renlm.graph.modular.ds.mapper.DsErRelMapper;
import cn.renlm.graph.modular.ds.service.IDsErRelService;

/**
 * <p>
 * 数据源-ER模型关系 服务实现类
 * </p>
 *
 * @author Renlm
 * @since 2022-04-29
 */
@Service
public class DsErRelServiceImpl extends ServiceImpl<DsErRelMapper, DsErRel> implements IDsErRelService {

}
