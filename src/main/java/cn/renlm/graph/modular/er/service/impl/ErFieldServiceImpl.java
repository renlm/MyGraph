package cn.renlm.graph.modular.er.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import cn.renlm.graph.modular.er.entity.Er;
import cn.renlm.graph.modular.er.entity.ErField;
import cn.renlm.graph.modular.er.mapper.ErFieldMapper;
import cn.renlm.graph.modular.er.service.IErFieldService;
import cn.renlm.graph.modular.er.service.IErService;

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

	@Autowired
	private IErService iErService;

	@Override
	public List<ErField> findListByErUuid(String erUuid) {
		if (StrUtil.isBlank(erUuid)) {
			return CollUtil.newArrayList();
		}
		Er er = iErService.getOne(Wrappers.<Er>lambdaQuery().eq(Er::getUuid, erUuid));
		if (er == null) {
			return CollUtil.newArrayList();
		}
		return this.list(Wrappers.<ErField>lambdaQuery().func(wrapper -> {
			wrapper.eq(ErField::getErId, er.getId());
			wrapper.eq(ErField::getDeleted, false);
			wrapper.orderByAsc(ErField::getId);
		}));
	}
}