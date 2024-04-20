package cn.renlm.mygraph.amqp;

import java.lang.reflect.Method;
import java.util.Date;

import jakarta.annotation.Resource;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.ExchangeBuilder;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;

import cn.hutool.core.codec.Base64;
import cn.hutool.core.exceptions.ExceptionUtil;
import cn.hutool.core.util.ReflectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.asymmetric.KeyType;
import cn.hutool.crypto.asymmetric.RSA;
import cn.hutool.extra.spring.SpringUtil;
import cn.renlm.mygraph.modular.sys.entity.SysFile;
import cn.renlm.mygraph.modular.sys.service.ISysFileService;
import lombok.extern.slf4j.Slf4j;

/**
 * Excel导出队列
 * 
 * @author RenLiMing(任黎明)
 *
 */
@Slf4j
@Configuration
public class ExcelExportQueue {

	private static final String KEY = "ExcelExport";

	public static final String EXCHANGE = KEY + AmqpUtil.Exchange;

	public static final String QUEUE = KEY + AmqpUtil.Queue;

	public static final String ROUTINGKEY = QUEUE + AmqpUtil.RoutingKey;

	@Resource
	private RSA rsa;

	@Autowired
	private ISysFileService iSysFileService;

	/**
	 * 执行表格导出
	 * 
	 * @param fileId
	 */
	@RabbitListener(bindings = {
			@QueueBinding(value = @Queue(value = QUEUE, durable = Exchange.TRUE), exchange = @Exchange(value = EXCHANGE, type = ExchangeTypes.DIRECT), key = ROUTINGKEY) })
	public void receiveMessage(String fileId) {
		if (StrUtil.isBlank(fileId)) {
			log.error("无效表格导出任务，{}", fileId);
			return;
		}
		SysFile sysFile = iSysFileService.getOne(Wrappers.<SysFile>lambdaQuery().eq(SysFile::getFileId, fileId));
		if (sysFile == null) {
			log.error("无效表格导出任务，{}", fileId);
			return;
		}
		// 更新状态
		sysFile.setStatus(3);
		sysFile.setUpdatedAt(new Date());
		iSysFileService.updateById(sysFile);
		try {
			String actuator = rsa.decryptStr(Base64.decodeStr(sysFile.getActuator()), KeyType.PublicKey);
			// 执行任务
			Class<?> clazz = Class.forName(actuator.substring(0, actuator.lastIndexOf(StrUtil.DOT)));
			String methodName = actuator.substring(actuator.lastIndexOf(StrUtil.DOT) + 1);
			Method method = ReflectUtil.getMethod(clazz, methodName, SysFile.class);
			Object service = SpringUtil.getBean(clazz);
			ReflectUtil.invoke(service, method, sysFile);
			// 更新状态
			sysFile.setStatus(5);
			sysFile.setUpdatedAt(new Date());
			iSysFileService.updateById(sysFile);
		} catch (Exception e) {
			e.printStackTrace();
			// 更新状态
			sysFile.setStatus(4);
			sysFile.setUpdatedAt(new Date());
			sysFile.setMessage(ExceptionUtil.stacktraceToString(e));
			iSysFileService.updateById(sysFile);
		}
	}

	/**
	 * 声明交换机
	 * 
	 * @return
	 */
	@Bean(name = EXCHANGE)
	public DirectExchange exchange() {
		return ExchangeBuilder.directExchange(EXCHANGE).durable(true).build();
	}

	/**
	 * 声明队列
	 * 
	 * @return
	 */
	@Bean(name = QUEUE)
	public org.springframework.amqp.core.Queue queue() {
		return QueueBuilder.durable(QUEUE).build();
	}

	/**
	 * 绑定队列到交换机
	 * 
	 * @param exchange
	 * @param queue
	 * @return
	 */
	@Bean(name = ROUTINGKEY)
	public Binding binding(@Qualifier(EXCHANGE) DirectExchange exchange,
			@Qualifier(QUEUE) org.springframework.amqp.core.Queue queue) {
		return BindingBuilder.bind(queue).to(exchange).with(ROUTINGKEY);
	}
}