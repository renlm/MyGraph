package cn.renlm.graph.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Data;

/**
 * 自定义配置
 * 
 * @author RenLiMing(任黎明)
 *
 */
@Data
@Component
@ConfigurationProperties(prefix = "my.config")
public class MyConfigProperties {

	private String ctx;

	private String wsHost;

	private String wssHost;

	private String wsAllowedOrigins;

	private Rsa rsa = new Rsa();

	private Chrome chrome = new Chrome();

	@Data
	public static class Rsa {

		private String privateKeyStr = "MIICeAIBADANBgkqhkiG9w0BAQEFAASCAmIwggJeAgEAAoGBALEAMFGK6UplQib58+zjHu6To4bwJFA3U7NkM5rco0StUke1VEr653mKteuUrWLPWnVBfa7itYGFVGLIz7O7MJF3KdCRv/mg1RYBTaa5uE0WBV6i3/oB4qQM/IaVmkOQe4M3wCs0pk//xDDOiiDUjiu98xvsufOcFA/5xYalhi6tAgMBAAECgYEAr1Q2iK+HQ7BmBOHXpDGedEk+1O2Pqv4DJVH+ZEpmWCMvu+R8qQE8xYKHjeJqldQ8EKZc7x2XGcOOwYOVar3j7qoxXE2DhqlBhPVIiIDlOmszGJeRbPzf/VLVooFJOfx2WX0CjTPhr8UiQZAVRswskQNY7+Fk7N3AOnl7SLZGC+kCQQD07Se2VFX/5W4VqatC/iV3S/Q0DOXZQ+pPrnEUubvR0zithhhOhCTTx7Suj5j9/latqpO/n4tygdAst7qNMfl/AkEAuQDX9kKaY9oDddIQ7Rw1Ap8PA3fle6QqrR4MM4ABdMmhEcE058tJyRGqj719mmc942xJQuQzCqiHXBdW7/H10wJBAKeWmmS04j99kky6Utg9JA+z5f2zkZaPVQV+nBuNVwmkmJSLO5iF7NkIPgZvdUTeQhsEq6IjnfwU/QeME9bHCisCQCTlJCy6j6vGONZwdGu1KmVBb28TpDKKRPCNg4Vpy+CMrZCq5XAsvKRa35cXBUubt135pIqwk/VWMtiM1wu3R30CQQCq5/zznX67g2Z0p87Dqo2Iejnru2avmj5FKX5Yr/Em313J89s0cbe2iRwrgRekB6sbj3VQXp66hz5N44f3zUVZ";

		private String publicKeyStr = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCxADBRiulKZUIm+fPs4x7uk6OG8CRQN1OzZDOa3KNErVJHtVRK+ud5irXrlK1iz1p1QX2u4rWBhVRiyM+zuzCRdynQkb/5oNUWAU2mubhNFgVeot/6AeKkDPyGlZpDkHuDN8ArNKZP/8Qwzoog1I4rvfMb7LnznBQP+cWGpYYurQIDAQAB";

	}

	@Data
	public static class Chrome {

		private String driverPath = "/usr/bin/chromedriver";

		private String sleepTime = "2500";

	}

}
