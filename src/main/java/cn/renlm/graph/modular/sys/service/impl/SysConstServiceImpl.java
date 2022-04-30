package cn.renlm.graph.modular.sys.service.impl;

import javax.annotation.Resource;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.BooleanUtil;
import cn.hutool.core.util.StrUtil;
import cn.renlm.graph.common.CacheKey;
import cn.renlm.graph.common.ConstVal;
import cn.renlm.graph.modular.sys.entity.SysConst;
import cn.renlm.graph.modular.sys.mapper.SysConstMapper;
import cn.renlm.graph.modular.sys.service.ISysConstService;

/**
 * <p>
 * 系统常量 服务实现类
 * </p>
 *
 * @author Renlm
 * @since 2022-04-29
 */
@Service
public class SysConstServiceImpl extends ServiceImpl<SysConstMapper, SysConst> implements ISysConstService {

	@Resource
	private RedisTemplate<String, String> redisTemplate;

	@Override
	public String getValue(String code) {
		Object value = redisTemplate.opsForHash().get(CacheKey.SysConst.name(), code);
		if (StrUtil.isBlankIfStr(value)) {
			SysConst sysConst = this.getOne(Wrappers.<SysConst>lambdaQuery().eq(SysConst::getCode, code));
			if (sysConst == null) {
				return StrUtil.EMPTY;
			}
			redisTemplate.opsForHash().put(CacheKey.SysConst.name(), code, sysConst.getVal());
			return sysConst.getVal();
		} else {
			return Convert.toStr(value);
		}
	}

	@Override
	public Boolean getCfgEnableRegistration() {
		String cfgEnableRegistration = this.getValue(ConstVal.Sys.cfgEnableRegistration.name());
		return BooleanUtil.toBoolean(cfgEnableRegistration);
	}
}
