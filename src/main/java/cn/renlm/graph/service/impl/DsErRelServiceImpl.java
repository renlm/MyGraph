package cn.renlm.graph.service.impl;

import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import cn.renlm.graph.entity.DsErRel;
import cn.renlm.graph.mapper.DsErRelMapper;
import cn.renlm.graph.service.IDsErRelService;

/**
 * <p>
 * 数据源-ER模型关系 服务实现类
 * </p>
 *
 * @author Renlm
 * @since 2022-03-04
 */
@Service
public class DsErRelServiceImpl extends ServiceImpl<DsErRelMapper, DsErRel> implements IDsErRelService {

}
