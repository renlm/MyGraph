package cn.renlm.graph.config;

import static com.github.mkopylec.charon.configuration.CharonConfigurer.charonConfiguration;
import static com.github.mkopylec.charon.configuration.RequestMappingConfigurer.requestMapping;
import static com.github.mkopylec.charon.forwarding.RestTemplateConfigurer.restTemplate;
import static com.github.mkopylec.charon.forwarding.TimeoutConfigurer.timeout;
import static com.github.mkopylec.charon.forwarding.interceptors.async.AsynchronousForwarderConfigurer.asynchronousForwarder;
import static com.github.mkopylec.charon.forwarding.interceptors.rewrite.RequestServerNameRewriterConfigurer.requestServerNameRewriter;
import static java.time.Duration.ofMinutes;
import static java.time.Duration.ofSeconds;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.github.mkopylec.charon.configuration.CharonConfigurer;

import cn.renlm.graph.util.MyConfigProperties;

/**
 * 代理配置
 * 
 * @author Renlm
 *
 */
@Configuration
class CharonConfiguration {

	@Bean
	CharonConfigurer charonConfigurer(MyConfigProperties mcp) {
		return charonConfiguration()
				.set(requestServerNameRewriter().outgoingServers(mcp.getCtx()))
				.set(restTemplate().set(timeout().connection(ofSeconds(1)).read(ofMinutes(10)).write(ofMinutes(10))))
				.add(requestMapping("proxy")
						.pathRegex("/proxy/.*")
						.set(requestServerNameRewriter().outgoingServers("http://localhost:9081"))
						.set(asynchronousForwarder()));
	}
}