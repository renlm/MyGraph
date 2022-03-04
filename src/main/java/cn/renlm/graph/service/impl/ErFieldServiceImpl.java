package cn.renlm.graph.service.impl;

import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import cn.renlm.graph.entity.ErField;
import cn.renlm.graph.mapper.ErFieldMapper;
import cn.renlm.graph.service.IErFieldService;

/**
 * <p>
 * ER模型-字段 服务实现类
 * </p>
 *
 * @author Renlm
 * @since 2022-03-04
 */
@Service
public class ErFieldServiceImpl extends ServiceImpl<ErFieldMapper, ErField> implements IErFieldService {

}
