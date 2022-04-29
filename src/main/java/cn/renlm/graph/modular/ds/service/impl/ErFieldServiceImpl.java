package cn.renlm.graph.modular.ds.service.impl;

import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import cn.renlm.graph.modular.ds.entity.ErField;
import cn.renlm.graph.modular.ds.mapper.ErFieldMapper;
import cn.renlm.graph.modular.ds.service.IErFieldService;

/**
 * <p>
 * ER模型-字段 服务实现类
 * </p>
 *
 * @author Renlm
 * @since 2022-04-29
 */
@Service
public class ErFieldServiceImpl extends ServiceImpl<ErFieldMapper, ErField> implements IErFieldService {

}
