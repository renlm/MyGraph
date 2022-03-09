package cn.renlm.graph.service.impl;

import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import cn.hutool.core.codec.Base64;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.db.meta.Column;
import cn.hutool.db.meta.Table;
import cn.hutool.db.meta.TableType;
import cn.renlm.graph.common.Result;
import cn.renlm.graph.dto.DsDto;
import cn.renlm.graph.dto.UserDto;
import cn.renlm.graph.entity.Ds;
import cn.renlm.graph.entity.DsErRel;
import cn.renlm.graph.entity.Er;
import cn.renlm.graph.entity.ErField;
import cn.renlm.graph.mapper.DsMapper;
import cn.renlm.graph.service.IDsErRelService;
import cn.renlm.graph.service.IDsService;
import cn.renlm.graph.service.IErFieldService;
import cn.renlm.graph.service.IErService;
import cn.renlm.plugins.MyUtil.MyDbUtil;

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

	@Autowired
	private IDsErRelService iDsErRelService;

	@Autowired
	private IErService iErService;

	@Autowired
	private IErFieldService iErFieldService;

	@Override
	public Page<Ds> findPage(Page<Ds> page, UserDto user, DsDto form) {
		return this.page(page, Wrappers.<Ds>lambdaQuery().func(wrapper -> {
			wrapper.eq(Ds::getCreatorUserId, user.getUserId());
			wrapper.eq(Ds::getDeleted, false);
			wrapper.orderByDesc(Ds::getUpdatedAt);
			wrapper.orderByDesc(Ds::getId);
			if (StrUtil.isNotBlank(form.getKeywords())) {
				wrapper.and(item -> {
					item.or().like(Ds::getUrl, form.getKeywords());
					item.or().like(Ds::getSchema, form.getKeywords());
					item.or().like(Ds::getUsername, form.getKeywords());
					item.or().like(Ds::getRemark, form.getKeywords());
				});
			}
		}));
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public Result init(UserDto user, DsDto ds) {
		List<Table> tables = MyDbUtil.getTableMetas(ds.getUrl(), ds.getSchema(), ds.getUsername(), ds.getPassword(),
				TableType.TABLE);
		// 保存数据源
		ds.setPassword(Base64.encodeUrlSafe(ds.getPassword()));
		if (StrUtil.isBlank(ds.getUuid())) {
			ds.setUuid(IdUtil.simpleUUID().toUpperCase());
			ds.setCreatedAt(new Date());
			ds.setCreatorUserId(user.getUserId());
			ds.setCreatorNickname(user.getNickname());
			ds.setUpdatedAt(ds.getCreatedAt());
			ds.setDeleted(false);
		} else {
			Ds entity = this.getOne(Wrappers.<Ds>lambdaQuery().eq(Ds::getUuid, ds.getUuid()));
			ds.setId(entity.getId());
			ds.setCreatedAt(entity.getCreatedAt());
			ds.setCreatorUserId(entity.getCreatorUserId());
			ds.setCreatorNickname(entity.getCreatorNickname());
			ds.setUpdatedAt(new Date());
			ds.setUpdatorUserId(user.getUserId());
			ds.setUpdatorNickname(user.getNickname());
			ds.setDeleted(entity.getDeleted());
		}
		this.saveOrUpdate(ds);
		// 保存ER模型（删除原有数据，重新插入）
		iErFieldService.update(Wrappers.<ErField>lambdaUpdate().func(wrapper -> {
			wrapper.set(ErField::getDeleted, true);
			wrapper.set(ErField::getUpdatedAt, new Date());
			wrapper.set(ErField::getUpdatorUserId, user.getUserId());
			wrapper.set(ErField::getUpdatorNickname, user.getNickname());
			wrapper.eq(ErField::getDeleted, false);
			wrapper.inSql(ErField::getId, StrUtil.indexedFormat(
					"select ef.id from ds_er_rel der, er e, er_field ef where der.deleted = false and e.deleted = false and ef.deleted = false and der.er_id = e.id and e.id = ef.er_id and der.ds_id = {0}",
					ds.getId()));
		}));
		iErService.update(Wrappers.<Er>lambdaUpdate().func(wrapper -> {
			wrapper.set(Er::getDeleted, true);
			wrapper.set(Er::getUpdatedAt, new Date());
			wrapper.set(Er::getUpdatorUserId, user.getUserId());
			wrapper.set(Er::getUpdatorNickname, user.getNickname());
			wrapper.eq(Er::getDeleted, false);
			wrapper.inSql(Er::getId, StrUtil.indexedFormat(
					"select e.id from ds_er_rel der, er e where der.deleted = false and e.deleted = false and der.er_id = e.id and der.ds_id = {0}",
					ds.getId()));
		}));
		iDsErRelService.update(Wrappers.<DsErRel>lambdaUpdate().func(wrapper -> {
			wrapper.set(DsErRel::getDeleted, true);
			wrapper.set(DsErRel::getUpdatedAt, new Date());
			wrapper.set(DsErRel::getUpdatorUserId, user.getUserId());
			wrapper.set(DsErRel::getUpdatorNickname, user.getNickname());
			wrapper.eq(DsErRel::getDeleted, false);
			wrapper.eq(DsErRel::getDsId, ds.getId());
		}));
		if (CollUtil.isNotEmpty(tables)) {
			List<ErField> erFields = CollUtil.newArrayList();
			for (Table table : tables) {
				ds.setSchema(table.getSchema());
				// ER模型
				Er er = new Er();
				er.setUuid(IdUtil.simpleUUID().toUpperCase());
				er.setTableName(table.getTableName());
				er.setComment(table.getComment());
				er.setCreatedAt(new Date());
				er.setCreatorUserId(user.getUserId());
				er.setCreatorNickname(user.getNickname());
				er.setUpdatedAt(er.getCreatedAt());
				er.setDeleted(false);
				iErService.save(er);
				// 数据源-ER模型关系
				DsErRel dsErRel = new DsErRel();
				dsErRel.setDsId(ds.getId());
				dsErRel.setErId(er.getId());
				dsErRel.setCreatedAt(new Date());
				dsErRel.setCreatorUserId(user.getUserId());
				dsErRel.setCreatorNickname(user.getNickname());
				dsErRel.setDeleted(false);
				iDsErRelService.save(dsErRel);
				// 表字段
				Collection<Column> columns = table.getColumns();
				for (Column column : columns) {
					ErField erField = new ErField();
					erField.setErId(er.getId());
					erField.setUuid(IdUtil.simpleUUID().toUpperCase());
					erField.setName(column.getName());
					erField.setComment(column.getComment());
					erField.setType(column.getType());
					erField.setTypeName(column.getTypeName());
					erField.setSize(column.getSize());
					erField.setDigit(column.getDigit());
					erField.setIsNullable(column.isNullable());
					erField.setAutoIncrement(column.isAutoIncrement());
					erField.setColumnDef(column.getColumnDef());
					erField.setIsPk(column.isPk());
					erField.setIsFk(false);
					erField.setCreatedAt(new Date());
					erField.setCreatorUserId(user.getUserId());
					erField.setCreatorNickname(user.getNickname());
					erField.setUpdatedAt(erField.getCreatedAt());
					erField.setDeleted(false);
					erFields.add(erField);
				}
			}
			iErFieldService.saveBatch(erFields);
			this.updateById(ds);
		}
		return Result.success();
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void delByUuid(UserDto user, String uuid) {
		Ds ds = this.getOne(Wrappers.<Ds>lambdaQuery().eq(Ds::getUuid, uuid));
		// 删除表字段
		iErFieldService.update(Wrappers.<ErField>lambdaUpdate().func(wrapper -> {
			wrapper.set(ErField::getDeleted, true);
			wrapper.set(ErField::getUpdatedAt, new Date());
			wrapper.set(ErField::getUpdatorUserId, user.getUserId());
			wrapper.set(ErField::getUpdatorNickname, user.getNickname());
			wrapper.eq(ErField::getDeleted, false);
			wrapper.inSql(ErField::getId, StrUtil.indexedFormat(
					"select ef.id from ds_er_rel der, er e, er_field ef where der.deleted = false and e.deleted = false and ef.deleted = false and der.er_id = e.id and e.id = ef.er_id and der.ds_id = {0}",
					ds.getId()));
		}));
		// 删除ER模型
		iErService.update(Wrappers.<Er>lambdaUpdate().func(wrapper -> {
			wrapper.set(Er::getDeleted, true);
			wrapper.set(Er::getUpdatedAt, new Date());
			wrapper.set(Er::getUpdatorUserId, user.getUserId());
			wrapper.set(Er::getUpdatorNickname, user.getNickname());
			wrapper.eq(Er::getDeleted, false);
			wrapper.inSql(Er::getId, StrUtil.indexedFormat(
					"select e.id from ds_er_rel der, er e where der.deleted = false and e.deleted = false and der.er_id = e.id and der.ds_id = {0}",
					ds.getId()));
		}));
		// 删除数据源-ER模型关系
		iDsErRelService.update(Wrappers.<DsErRel>lambdaUpdate().func(wrapper -> {
			wrapper.set(DsErRel::getDeleted, true);
			wrapper.set(DsErRel::getUpdatedAt, new Date());
			wrapper.set(DsErRel::getUpdatorUserId, user.getUserId());
			wrapper.set(DsErRel::getUpdatorNickname, user.getNickname());
			wrapper.eq(DsErRel::getDeleted, false);
			wrapper.eq(DsErRel::getDsId, ds.getId());
		}));
		// 删除数据源
		ds.setUpdatedAt(new Date());
		ds.setUpdatorUserId(user.getUserId());
		ds.setUpdatorNickname(user.getNickname());
		ds.setDeleted(true);
		this.updateById(ds);
	}
}
