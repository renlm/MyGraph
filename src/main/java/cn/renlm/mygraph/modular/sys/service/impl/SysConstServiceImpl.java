package cn.renlm.mygraph.modular.sys.service.impl;

import jakarta.annotation.Resource;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.StrUtil;
import cn.renlm.mygraph.common.CacheKey;
import cn.renlm.mygraph.modular.sys.entity.SysConst;
import cn.renlm.mygraph.modular.sys.mapper.SysConstMapper;
import cn.renlm.mygraph.modular.sys.service.ISysConstService;

/**
 * <p>
 * 系统常量 服务实现类
 * </p>
 *
 * @author RenLiMing(任黎明)
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
}
