package cn.renlm.graph.service.impl;

import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import cn.renlm.graph.entity.Ds;
import cn.renlm.graph.mapper.DsMapper;
import cn.renlm.graph.service.IDsService;

/**
 * <p>
 * 数据源 服务实现类
 * </p>
 *
 * @author Renlm
 * @since 2022-03-04
 */
@Service
public class DsServiceImpl extends ServiceImpl<DsMapper, Ds> implements IDsService {

}
