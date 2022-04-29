package cn.renlm.graph.controller.sys;

import java.util.Date;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import cn.renlm.crawler.Result;
import cn.renlm.crawler.consts.CacheKey;
import cn.renlm.crawler.sys.entity.SysConst;
import cn.renlm.crawler.sys.service.ISysConstService;

/**
 * 系统常量
 * 
 * @author Renlm
 *
 */
@Controller
@RequestMapping("/sys/const")
public class SysConstController {

	@Autowired
	private ISysConstService iSysConstService;

	@Resource
	private RedisTemplate<String, String> redisTemplate;

	/**
	 * 基础配置
	 * 
	 * @return
	 */
	@RequestMapping
	public String index() {
		return "sys/const";
	}

	/**
	 * 配置列表
	 * 
	 * @param page
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/ajax/page")
	public Page<SysConst> page(Page<SysConst> page) {
		return iSysConstService.page(page, Wrappers.<SysConst>lambdaQuery().func(wrapper -> {
			wrapper.orderByAsc(SysConst::getSort);
			wrapper.orderByAsc(SysConst::getConstId);
		}));
	}

	/**
	 * 保存
	 * 
	 * @param constId
	 * @param val
	 * @param sort
	 * @param remark
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/ajax/save")
	public Result ajaxSave(String constId, String val, Integer sort, String remark) {
		try {
			SysConst entity = iSysConstService.getById(constId);
			entity.setVal(val);
			entity.setSort(sort);
			entity.setRemark(remark);
			entity.setUpdatedAt(new Date());
			iSysConstService.updateById(entity);
			redisTemplate.opsForHash().put(CacheKey.SysConst.name(), entity.getCode(), val);
			return Result.success();
		} catch (Exception e) {
			e.printStackTrace();
			return Result.error("服务器出错了");
		}
	}
}