package cn.renlm.graph.modular.er.service.impl;

import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import cn.hutool.core.util.StrUtil;
import cn.renlm.graph.dto.User;
import cn.renlm.graph.modular.er.dto.ErFieldLibDto;
import cn.renlm.graph.modular.er.entity.ErFieldLib;
import cn.renlm.graph.modular.er.mapper.ErFieldLibMapper;
import cn.renlm.graph.modular.er.service.IErFieldLibService;

/**
 * <p>
 * ER模型-我的字段库 服务实现类
 * </p>
 *
 * @author Renlm
 * @since 2022-05-13
 */
@Service
public class ErFieldLibServiceImpl extends ServiceImpl<ErFieldLibMapper, ErFieldLib> implements IErFieldLibService {

	@Override
	public Page<ErFieldLib> findPage(Page<ErFieldLib> page, User user, ErFieldLibDto form) {
		return this.page(page, Wrappers.<ErFieldLib>lambdaQuery().func(wrapper -> {
			if (StrUtil.isNotBlank(form.getKeywords())) {
				wrapper.and(item -> {
					item.or().like(ErFieldLib::getName, form.getKeywords());
					item.or().like(ErFieldLib::getComment, form.getKeywords());
				});
			}
			wrapper.eq(ErFieldLib::getCreatorUserId, user.getUserId());
			wrapper.eq(ErFieldLib::getDeleted, false);
			wrapper.orderByDesc(ErFieldLib::getUpdatedAt);
			wrapper.orderByDesc(ErFieldLib::getId);
		}));
	}
}
