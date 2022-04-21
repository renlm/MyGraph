package cn.renlm.graph.oshi;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;

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
	public static final long validityMillis = 1000 * 60 * 60 * 12;

	/**
	 * 获取服务器列表
	 * 
	 * @param ip
	 * @return
	 */
	public static final Map<String, OshiInfo> machines() {
		return null;
	}

	/**
	 * 获取监控数据
	 * 
	 * @param ip
	 * @return
	 */
	public static final List<OshiInfo> get(String ip) {
		return null;
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

		// 缓存数据
		RedisTemplate<String, OshiInfo> redisTemplate = getRedisTemplate();
		ZSetOperations<String, OshiInfo> zops = redisTemplate.opsForZSet();
		Long expTime = DateUtil.current() + validityMillis;
		zops.add(key, info, expTime);
		return info;
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