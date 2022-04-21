package cn.renlm.graph.oshi;

import java.io.File;
import java.math.BigDecimal;
import java.util.Date;

import cn.hutool.core.util.IdUtil;
import cn.hutool.system.HostInfo;
import cn.hutool.system.JavaInfo;
import cn.hutool.system.OsInfo;
import cn.hutool.system.SystemUtil;
import cn.hutool.system.oshi.CpuInfo;
import cn.hutool.system.oshi.OshiUtil;
import lombok.experimental.UtilityClass;
import oshi.hardware.GlobalMemory;

/**
 * Oshi 工具
 * 
 * @author Renlm
 *
 */
@UtilityClass
public class OshiInfoUtil {

	/**
	 * 采集服务器信息
	 * 
	 * @return
	 */
	public static final OshiInfo get() {
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
		File[] disks = File.listRoots();
		for (File file : disks) {
			disk += file.getTotalSpace();
			freeSpace += file.getFreeSpace();
		}

		info.setIp(new HostInfo().getAddress());
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
		return info;
	}
}