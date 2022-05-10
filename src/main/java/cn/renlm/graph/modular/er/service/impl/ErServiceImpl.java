package cn.renlm.graph.modular.er.service.impl;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import cn.renlm.graph.dto.User;
import cn.renlm.graph.modular.er.dto.ErDto;
import cn.renlm.graph.modular.er.entity.Er;
import cn.renlm.graph.modular.er.entity.ErField;
import cn.renlm.graph.modular.er.mapper.ErMapper;
import cn.renlm.graph.modular.er.service.IErFieldService;
import cn.renlm.graph.modular.er.service.IErService;

/**
 * <p>
 * ER模型 服务实现类
 * </p>
 *
 * @author Renlm
 * @since 2022-04-29
 */
@Service
public class ErServiceImpl extends ServiceImpl<ErMapper, Er> implements IErService {

	@Autowired
	private IErFieldService iErFieldService;

	@Override
	public Page<ErDto> findPage(Page<Er> page, User user, ErDto form) {
		Page<Er> pager = this.page(page, Wrappers.<Er>lambdaQuery().func(wrapper -> {
			wrapper.eq(Er::getCreatorUserId, user.getUserId());
			wrapper.eq(Er::getDeleted, false);
			wrapper.orderByDesc(Er::getUpdatedAt);
			wrapper.orderByDesc(Er::getId);
			if (StrUtil.isNotBlank(form.getDsUuid())) {
				wrapper.inSql(Er::getId, StrUtil.indexedFormat(
						"select e.id from ds d, ds_er_rel der, er e where d.deleted = 0 and der.deleted = 0 and e.deleted = 0 and d.id = der.ds_id and der.er_id = e.id and d.uuid = ''{0}''",
						form.getDsUuid()));
			}
			if (StrUtil.isNotBlank(form.getKeywords())) {
				wrapper.and(item -> {
					item.or().like(Er::getTableName, form.getKeywords());
					item.or().like(Er::getTableName, form.getKeywords());
					item.or().like(Er::getRemark, form.getKeywords());
				});
			}
		}));
		Page<ErDto> result = new Page<>();
		BeanUtil.copyProperties(pager, result);
		Map<Long, ErDto> map = new LinkedHashMap<>();
		List<ErDto> records = pager.getRecords().stream().filter(Objects::nonNull).map(obj -> {
			ErDto dto = BeanUtil.copyProperties(obj, ErDto.class);
			dto.setFields(CollUtil.newArrayList());
			map.put(dto.getId(), dto);
			return dto;
		}).collect(Collectors.toList());
		result.setRecords(records);
		List<Long> erIds = records.stream().map(ErDto::getId).collect(Collectors.toList());
		if (CollUtil.isNotEmpty(erIds)) {
			List<ErField> fields = iErFieldService.list(Wrappers.<ErField>lambdaQuery().func(wrapper -> {
				wrapper.in(ErField::getErId, erIds);
				wrapper.orderByAsc(ErField::getId);
			}));
			fields.forEach(field -> {
				map.get(field.getErId()).getFields().add(field);
			});
		}
		return result;
	}

	@Override
	public List<ErDto> findListWithFields(List<String> uuids) {
		Map<Long, ErDto> map = new LinkedHashMap<>();
		List<ErDto> list = this.list(Wrappers.<Er>lambdaQuery().func(wrapper -> {
			wrapper.in(Er::getUuid, uuids);
			wrapper.orderByAsc(Er::getUpdatedAt);
			wrapper.orderByAsc(Er::getId);
		})).stream().filter(Objects::nonNull).map(obj -> {
			ErDto dto = BeanUtil.copyProperties(obj, ErDto.class);
			dto.setFields(CollUtil.newArrayList());
			map.put(dto.getId(), dto);
			return dto;
		}).collect(Collectors.toList());
		List<Long> erIds = list.stream().map(ErDto::getId).collect(Collectors.toList());
		List<ErField> fields = iErFieldService.list(Wrappers.<ErField>lambdaQuery().func(wrapper -> {
			wrapper.in(ErField::getErId, erIds);
			wrapper.orderByAsc(ErField::getId);
		}));
		fields.forEach(field -> {
			map.get(field.getErId()).getFields().add(field);
		});
		return list;
	}
}
