package cn.renlm.mygraph.util;

import java.util.List;
import java.util.stream.Collectors;

import org.lionsoul.ip2region.xdb.Searcher;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.io.resource.ResourceUtil;
import cn.hutool.core.lang.RegexPool;
import cn.hutool.core.net.Ipv4Util;
import cn.hutool.core.util.ReUtil;
import cn.hutool.core.util.StrUtil;
import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;

/**
 * IP归属地工具（离线）
 * 
 * @author RenLiMing(任黎明)
 *
 */
@UtilityClass
public class Ip2regionUtil {

	private static final String separator = "|";

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
		if (StrUtil.isBlankOrUndefined(ip)) {
			return null;
		} else if (!ReUtil.isMatch(RegexPool.IPV4, ip)) {
			return null;
		} else if (Ipv4Util.isInnerIP(ip)) {
			return null;
		}
		try {
			String region = searcher.search(ip);
			if (StrUtil.isBlank(region)) {
				return null;
			} else {
				List<String> strs = StrUtil.splitTrim(region, separator).stream().distinct().filter(s -> {
					if (StrUtil.isNumeric(s)) {
						return false;
					} else {
						return true;
					}
				}).collect(Collectors.toList());
				CollUtil.removeBlank(strs);
				return StrUtil.join(separator, strs.stream().filter(s -> {
					for (String t : strs) {
						if (StrUtil.startWithIgnoreEquals(t, s)) {
							return false;
						}
					}
					return true;
				}).collect(Collectors.toList()));
			}
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

}
