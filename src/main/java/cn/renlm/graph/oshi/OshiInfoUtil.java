package cn.renlm.graph.oshi;

import java.math.BigDecimal;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import cn.hutool.system.JavaInfo;
import cn.hutool.system.OsInfo;
import cn.hutool.system.SystemUtil;
import cn.hutool.system.oshi.CpuInfo;
import cn.hutool.system.oshi.OshiUtil;
import lombok.experimental.UtilityClass;
import oshi.hardware.GlobalMemory;
import oshi.software.os.FileSystem;
import oshi.software.os.OSFileStore;
import oshi.software.os.OperatingSystem;

/**
 * Oshi 工具
 * 
 * @author Renlm
 *
 */
@UtilityClass
public class OshiInfoUtil {

	/**
	 * 缓存键
	 */
	private static final String key = OshiInfoUtil.class.getName();

	/**
	 * 采集频率
	 */
	public static final String cron = "*/5 * * * * ?";

	/**
	 * 缓存时长（毫秒）
	 */
	public static final int validityMillis = 1000 * 60 * 60 * 1;

	/**
	 * 获取服务器列表
	 * 
	 * @return
	 */
	public static final Map<String, OshiInfo> servers() {
		// 查询区间
		Date now = new Date();
		long min = now.getTime();
		long max = DateUtil.offsetMillisecond(now, validityMillis).getTime();

		// 缓存服务器列表
		Map<String, OshiInfo> map = new LinkedHashMap<>();
		RedisTemplate<String, String> redisTemplateIp = getRedisTemplate();
		ZSetOperations<String, String> zopsIp = redisTemplateIp.opsForZSet();
		Set<String> ips = zopsIp.rangeByScore(key, min, max);

		// 缓存监控数据
		RedisTemplate<String, OshiInfo> redisTemplateOshi = getRedisTemplate();
		ZSetOperations<String, OshiInfo> zopsOshi = redisTemplateOshi.opsForZSet();
		for (String ip : ips) {
			String oshiKey = getOshiKey(ip);
			Set<OshiInfo> infos = zopsOshi.rangeByScore(oshiKey, min, max, 0, 1);
			if (CollUtil.isNotEmpty(infos)) {
				map.put(ip, CollUtil.getFirst(infos));
			}
		}
		return map;
	}

	/**
	 * 获取监控数据
	 * 
	 * @param ip
	 * @return
	 */
	public static final Set<OshiInfo> get(String ip) {
		// 查询区间
		Date now = new Date();
		long min = now.getTime();
		long max = DateUtil.offsetMillisecond(now, validityMillis).getTime();

		// 缓存监控数据
		RedisTemplate<String, OshiInfo> redisTemplate = getRedisTemplate();
		ZSetOperations<String, OshiInfo> zops = redisTemplate.opsForZSet();
		String oshiKey = getOshiKey(ip);
		Set<OshiInfo> infos = zops.rangeByScore(oshiKey, min, max);
		return infos;
	}

	/**
	 * 采集服务器信息
	 * 
	 * @return
	 */
	public static final OshiInfo collect() {
		OshiInfo info = new OshiInfo();

		// Cpu信息
		CpuInfo cpuInfo = OshiUtil.getCpuInfo();
		Integer cpuCores = cpuInfo.getCpuNum();
		double cpuUsedRate = cpuInfo.getUsed();

		// 系统内存信息
		GlobalMemory globalMemory = OshiUtil.getMemory();
		long totalByte = globalMemory.getTotal();
		long acaliableByte = globalMemory.getAvailable();

		// 操作系统
		OsInfo osInfo = SystemUtil.getOsInfo();
		String osName = osInfo.getName();
		String osArch = osInfo.getArch();

		// Jvm信息
		JavaInfo javaInfo = SystemUtil.getJavaInfo();
		long jvmTotalMemoryByte = SystemUtil.getTotalMemory();
		long freeMemoryByte = SystemUtil.getFreeMemory();
		String javaVersion = javaInfo.getVersion();

		// 磁盘信息
		long disk = 0L;
		long freeSpace = 0L;
		OperatingSystem operatingSystem = OshiUtil.getOs();
		FileSystem fileSystem = operatingSystem.getFileSystem();
		List<OSFileStore> fsArray = fileSystem.getFileStores();
		for (OSFileStore fs : fsArray) {
			disk += fs.getTotalSpace();
			freeSpace += fs.getFreeSpace();
		}

		// 封装数据
		info.setIp(SystemUtil.getHostInfo().getAddress());
		info.setTime(new Date());
		info.setUid(IdUtil.getSnowflakeNextId());
		info.setCpuCores(cpuCores);
		info.setCpuUsedRate(new BigDecimal(cpuUsedRate).setScale(2, BigDecimal.ROUND_HALF_UP));
		info.setMemory(totalByte);
		info.setMemoryUsed(totalByte - acaliableByte);
		info.setMemoryUsedRate(
				new BigDecimal(100.0 - (acaliableByte * 100.0 / totalByte)).setScale(2, BigDecimal.ROUND_HALF_UP));
		info.setDisk(disk);
		info.setDiskUsed(disk - freeSpace);
		info.setDiskUsedRate(new BigDecimal(100.0 - (freeSpace * 100.0 / disk)).setScale(2, BigDecimal.ROUND_HALF_UP));
		info.setOsName(osName);
		info.setOsArch(osArch);
		info.setJvmMemory(jvmTotalMemoryByte);
		info.setJvmMemoryUsed(jvmTotalMemoryByte - freeMemoryByte);
		info.setJvmMemoryUsedRate(new BigDecimal(100.0 - (freeMemoryByte * 100.0 / jvmTotalMemoryByte)).setScale(2,
				BigDecimal.ROUND_HALF_UP));
		info.setJavaVersion(javaVersion);

		// 过期时间
		Long expTime = info.getTime().getTime() + validityMillis;

		// 缓存服务器列表
		RedisTemplate<String, String> redisTemplateIp = getRedisTemplate();
		ZSetOperations<String, String> zopsIp = redisTemplateIp.opsForZSet();
		zopsIp.add(key, info.getIp(), expTime);

		// 缓存监控数据
		RedisTemplate<String, OshiInfo> redisTemplateOshi = getRedisTemplate();
		ZSetOperations<String, OshiInfo> zopsOshi = redisTemplateOshi.opsForZSet();
		String oshiKey = getOshiKey(info.getIp());
		long min = 0;
		long max = DateUtil.offsetMillisecond(info.getTime(), -validityMillis).getTime();
		zopsOshi.removeRangeByScore(oshiKey, min, max);
		zopsOshi.add(oshiKey, info, expTime);

		return info;
	}

	/**
	 * 缓存监控数据键
	 * 
	 * @param ip
	 * @return
	 */
	private static final String getOshiKey(String ip) {
		return key + StrUtil.AT + ip;
	}

	/**
	 * 获取Redis操作工具
	 * 
	 * @param <K>
	 * @param <V>
	 * @return
	 */
	private static final <K, V> RedisTemplate<K, V> getRedisTemplate() {
		return SpringUtil.getBean(StrUtil.lowerFirst(RedisTemplate.class.getSimpleName()));
	}
}