package cn.renlm.graph.dialect;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.thymeleaf.context.IExpressionContext;
import org.thymeleaf.dialect.AbstractDialect;
import org.thymeleaf.dialect.IExpressionObjectDialect;
import org.thymeleaf.expression.IExpressionObjectFactory;

import cn.renlm.graph.modular.sys.service.ISysConstService;
import cn.renlm.graph.modular.sys.service.ISysDictService;
import cn.renlm.graph.util.MyConfigProperties;

/**
 * 工具集
 * 
 * @author Renlm
 *
 */
@Component
public class DialectUtil extends AbstractDialect implements IExpressionObjectDialect {

	private final Map<String, Object> map = Collections.synchronizedMap(new HashMap<>());

	protected DialectUtil(MyConfigProperties myConfigProperties, ISysConstService iSysConstService,
			ISysDictService iSysDictService, Environment environment) {
		super(DialectUtil.class.getSimpleName());
		map.put("ConfigUtil", myConfigProperties);
		map.put("ConstUtil", iSysConstService);
		map.put("DictUtil", iSysDictService);
		map.put("ProfileUtil", environment.getActiveProfiles());
	}

	@Override
	public IExpressionObjectFactory getExpressionObjectFactory() {
		return new IExpressionObjectFactory() {
			@Override
			public boolean isCacheable(String expressionObjectName) {
				return map.containsKey(expressionObjectName);
			}

			@Override
			public Set<String> getAllExpressionObjectNames() {
				return map.keySet();
			}

			@Override
			public Object buildObject(IExpressionContext context, String expressionObjectName) {
				return map.get(expressionObjectName);
			}
		};
	}
}