package cn.renlm.graph.controller.er;

import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import cn.renlm.graph.amqp.AmqpUtil;
import cn.renlm.graph.amqp.GraphCoverQueue;
import cn.renlm.graph.dto.User;
import cn.renlm.graph.modular.er.dto.ErDto;
import cn.renlm.graph.modular.er.entity.Er;
import cn.renlm.graph.modular.er.service.IErService;
import cn.renlm.graph.modular.graph.dto.GraphDto;
import cn.renlm.graph.mxgraph.ERModelParser;
import cn.renlm.plugins.MyResponse.Datagrid;
import cn.renlm.plugins.MyResponse.Result;
import cn.renlm.graph.util.RedisUtil;

/**
 * ER模型
 * 
 * @author Renlm
 *
 */
@Controller
@RequestMapping("/er")
public class ErController {

	@Autowired
	private IErService iErService;

	@Autowired
	private ERModelParser eRModelParser;

	/**
	 * 分页列表
	 * 
	 * @param authentication
	 * @param page
	 * @param form
	 * @return
	 */
	@ResponseBody
	@GetMapping("/ajax/page")
	public Datagrid<ErDto> page(Authentication authentication, Page<Er> page, ErDto form) {
		User user = (User) authentication.getPrincipal();
		Page<ErDto> data = iErService.findPage(page, user, form);
		return Datagrid.of(data);
	}

	/**
	 * 创建ER模型图
	 * 
	 * @param authentication
	 * @param erUuids
	 * @param form
	 * @return
	 */
	@ResponseBody
	@PostMapping("/ajax/createGraph")
	public Result<?> createGraph(Authentication authentication, String erUuids, GraphDto form) {
		User user = (User) authentication.getPrincipal();
		try {
			Result<?> result = eRModelParser.create(user, StrUtil.splitTrim(erUuids, StrUtil.COMMA), form);
			if (result.isSuccess()) {
				String key = GraphCoverQueue.QUEUE + IdUtil.simpleUUID().toUpperCase();
				RedisTemplate<String, String> edisTemplate = RedisUtil.getRedisTemplate();
				edisTemplate.opsForValue().set(key, form.getUuid(), 7, TimeUnit.DAYS);
				AmqpUtil.createQueue(GraphCoverQueue.EXCHANGE, GraphCoverQueue.ROUTINGKEY, key);
				return Result.success().setMessage("ER图已生成，请到归属文档中查看");
			}
			return result;
		} catch (Exception e) {
			e.printStackTrace();
			return Result.error("出错了");
		}
	}
}