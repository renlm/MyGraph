package cn.renlm.graph.modular.ds.service.impl;

import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import cn.renlm.graph.modular.ds.entity.Ds;
import cn.renlm.graph.modular.ds.mapper.DsMapper;
import cn.renlm.graph.modular.ds.service.IDsService;

/**
 * <p>
 * 数据源 服务实现类
 * </p>
 *
 * @author Renlm
 * @since 2022-05-15
 */
@Service
public class DsServiceImpl extends ServiceImpl<DsMapper, Ds> implements IDsService {

}
