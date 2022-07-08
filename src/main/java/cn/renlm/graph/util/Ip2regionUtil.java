package cn.renlm.graph.util;

import org.lionsoul.ip2region.xdb.Searcher;

import cn.hutool.core.io.resource.ResourceUtil;
import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;

/**
 * IP归属地工具（离线）
 * 
 * @author Renlm
 *
 */
@UtilityClass
public class Ip2regionUtil {

	/**
	 * 数据文件
	 */
	private static final String XDB = "data/ip2region.xdb";

	/**
	 * 解析器
	 */
	private static final Searcher searcher = initSearcher();

	/**
	 * 初始化
	 * 
	 * @return
	 */
	@SneakyThrows
	private static final Searcher initSearcher() {
		Searcher mySearcher;
		synchronized (Ip2regionUtil.class) {
			byte[] cBuff = ResourceUtil.readBytes(XDB);
			mySearcher = Searcher.newWithBuffer(cBuff);
		}
		return mySearcher;
	}

	/**
	 * 解析ip归属地
	 * 
	 * @param ip
	 * @return
	 */
	public static final String parse(String ip) {
		try {
			return searcher.search(ip);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
}