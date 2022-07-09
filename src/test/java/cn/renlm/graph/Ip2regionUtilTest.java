package cn.renlm.graph;

import org.junit.jupiter.api.Test;

import cn.renlm.graph.util.Ip2regionUtil;
import lombok.extern.slf4j.Slf4j;

/**
 * IP归属地工具（离线）
 *
 * @author Renlm
 *
 */
@Slf4j
public class Ip2regionUtilTest {

	@Test
	public void test() {
		log.info(Ip2regionUtil.parse("124.207.107.136"));
	}
}