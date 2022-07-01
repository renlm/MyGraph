package cn.renlm.graph.config;

import static com.github.mkopylec.charon.configuration.CharonConfigurer.charonConfiguration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.github.mkopylec.charon.configuration.CharonConfigurer;

/**
 * 代理配置
 * 
 * @author Renlm
 *
 */
@Configuration
class CharonConfiguration {

	@Bean
	CharonConfigurer charonConfigurer() {
		return charonConfiguration();
	}
}