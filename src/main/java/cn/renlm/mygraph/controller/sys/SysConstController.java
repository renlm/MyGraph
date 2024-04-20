package cn.renlm.mygraph.controller.sys;

import java.util.Date;

import jakarta.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import cn.renlm.mygraph.common.CacheKey;
import cn.renlm.mygraph.common.Role;
import cn.renlm.mygraph.modular.sys.entity.SysConst;
import cn.renlm.mygraph.modular.sys.service.ISysConstService;
import cn.renlm.plugins.MyResponse.Datagrid;
import cn.renlm.plugins.MyResponse.Result;

/**
 * 系统常量
 * 
 * @author RenLiMing(任黎明)
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
	@GetMapping
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
	@GetMapping("/ajax/page")
	public Datagrid<SysConst> page(Page<SysConst> page) {
		Page<SysConst> data = iSysConstService.page(page, Wrappers.<SysConst>lambdaQuery().func(wrapper -> {
			wrapper.orderByAsc(SysConst::getSort);
			wrapper.orderByAsc(SysConst::getConstId);
		}));
		return Datagrid.of(data);
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
	@PostMapping("/ajax/save")
	@PreAuthorize(Role.AdminSpEL)
	public Result<?> ajaxSave(String constId, String val, Integer sort, String remark) {
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
			return Result.error("出错了");
		}
	}
}