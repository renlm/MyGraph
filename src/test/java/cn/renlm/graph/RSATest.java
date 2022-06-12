package cn.renlm.graph;

import org.junit.jupiter.api.Test;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.lang.Console;
import cn.hutool.crypto.asymmetric.KeyType;
import cn.hutool.crypto.asymmetric.RSA;
import cn.renlm.plugins.ConstVal;

/**
 * 非对称加密RSA
 * 
 * @author Renlm
 *
 */
public class RSATest {

	String pubPath = "/properties/dev/config/pub";
	String ascPath = "/properties/dev/config/pub.asc";

	/**
	 * 生成密钥对（公钥+私钥）
	 */
	@Test
	public void key() {
		RSA rsa = new RSA();
		Console.log(rsa.getPrivateKeyBase64());
		Console.log(rsa.getPublicKeyBase64());
	}

	/**
	 * 加密解密
	 */
	@Test
	public void test() {
		String publicKeyBase64 = FileUtil.readUtf8String(ConstVal.resourcesDir + pubPath);
		String privateKeyBase64 = FileUtil.readUtf8String(ConstVal.resourcesDir + ascPath);
		RSA rsa = new RSA(privateKeyBase64, publicKeyBase64);
		String encrypt = rsa.encryptBase64("abc", KeyType.PrivateKey);
		Console.log("加密：" + encrypt);
		String decrypt = rsa.decryptStr(encrypt, KeyType.PublicKey);
		Console.log("解密：" + decrypt);
	}
}