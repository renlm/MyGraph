package cn.renlm.graph.modular.er.service.impl;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.StrUtil;
import cn.renlm.graph.dto.User;
import cn.renlm.graph.modular.er.dto.ErFieldDto;
import cn.renlm.graph.modular.er.entity.Er;
import cn.renlm.graph.modular.er.entity.ErField;
import cn.renlm.graph.modular.er.entity.ErFieldLib;
import cn.renlm.graph.modular.er.mapper.ErFieldMapper;
import cn.renlm.graph.modular.er.service.IErFieldLibService;
import cn.renlm.graph.modular.er.service.IErFieldService;
import cn.renlm.graph.modular.er.service.IErService;

/**
 * <p>
 * ER模型-字段 服务实现类
 * </p>
 *
 * @author RenLiMing(任黎明)
 * @since 2022-04-29
 */
@Service
public class ErFieldServiceImpl extends ServiceImpl<ErFieldMapper, ErField> implements IErFieldService {

	@Autowired
	private IErService iErService;

	@Autowired
	private IErFieldLibService iErFieldLibService;

	@Override
	public List<ErFieldDto> findListByEr(User user, String erUuid) {
		if (StrUtil.isBlank(erUuid)) {
			return CollUtil.newArrayList();
		}
		Er er = iErService.getOne(Wrappers.<Er>lambdaQuery().eq(Er::getUuid, erUuid));
		if (er == null) {
			return CollUtil.newArrayList();
		}
		// 字段列表
		List<String> nameList = CollUtil.newArrayList();
		List<ErFieldDto> list = this.list(Wrappers.<ErField>lambdaQuery().func(wrapper -> {
			wrapper.eq(ErField::getErId, er.getId());
			wrapper.eq(ErField::getCreatorUserId, user.getUserId());
			wrapper.eq(ErField::getDeleted, false);
			wrapper.orderByAsc(ErField::getId);
		})).stream().filter(Objects::nonNull).map(obj -> {
			ErFieldDto data = BeanUtil.copyProperties(obj, ErFieldDto.class);
			nameList.add(data.getName());
			return data;
		}).collect(Collectors.toList());
		// 查询字段库
		Map<String, ErFieldLib> map = new LinkedHashMap<>();
		if (CollUtil.isNotEmpty(nameList)) {
			List<ErFieldLib> libs = iErFieldLibService.list(Wrappers.<ErFieldLib>lambdaQuery().func(wrapper -> {
				wrapper.in(ErFieldLib::getName, nameList);
				wrapper.eq(ErFieldLib::getCreatorUserId, user.getUserId());
				wrapper.eq(ErFieldLib::getDeleted, false);
			}));
			libs.forEach(lib -> {
				map.put(lib.getName(), lib);
			});
		}
		// 是否被收藏
		if (MapUtil.isNotEmpty(map)) {
			for (ErFieldDto field : list) {
				field.setLibing(map.containsKey(field.getName()));
			}
		}
		return list;
	}
}
