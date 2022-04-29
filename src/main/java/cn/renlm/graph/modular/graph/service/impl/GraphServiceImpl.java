package cn.renlm.graph.modular.graph.service.impl;

import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import cn.renlm.graph.modular.graph.entity.Graph;
import cn.renlm.graph.modular.graph.mapper.GraphMapper;
import cn.renlm.graph.modular.graph.service.IGraphService;

/**
 * <p>
 * 图形设计 服务实现类
 * </p>
 *
 * @author Renlm
 * @since 2022-04-29
 */
@Service
public class GraphServiceImpl extends ServiceImpl<GraphMapper, Graph> implements IGraphService {

}
