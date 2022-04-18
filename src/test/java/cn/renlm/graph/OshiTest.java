package cn.renlm.graph;

import java.util.List;

import org.junit.jupiter.api.Test;

import cn.hutool.system.JavaInfo;
import cn.hutool.system.OsInfo;
import cn.hutool.system.SystemUtil;
import cn.hutool.system.oshi.CpuInfo;
import cn.hutool.system.oshi.OshiUtil;
import lombok.extern.slf4j.Slf4j;
import oshi.hardware.GlobalMemory;
import oshi.hardware.HWDiskStore;
import oshi.hardware.HWPartition;
import oshi.util.FormatUtil;

/**
 * Oshi工具
 *
 * @author Renlm
 *
 */
@Slf4j
public class OshiTest {

	/**
	 * 取得OS的信息
	 */
	@Test
	public void osInfo() {
		OsInfo osInfo = SystemUtil.getOsInfo();
		log.info("操作系统：{}", osInfo.getName());
		log.info("系统架构：{}", osInfo.getArch());
	}

	/**
	 * 获取Cpu信息
	 */
	@Test
	public void cpuInfo() {
		CpuInfo cpuInfo = OshiUtil.getCpuInfo();
		log.info("Cpu 核心数：{}", cpuInfo.getCpuNum());
		log.info("Cpu 利用率：{} %", cpuInfo.getUsed());
	}

	/**
	 * 获取内存相关信息
	 */
	@Test
	public void globalMemory() {
		GlobalMemory globalMemory = OshiUtil.getMemory();
		log.info("系统内存：{}", FormatUtil.formatBytes(globalMemory.getTotal()));
		log.info("已使用系统内存：{}", FormatUtil.formatBytes(globalMemory.getAvailable()));
	}

	/**
	 * 获取磁盘相关信息
	 */
	@Test
	public void diskStores() {
		List<HWDiskStore> diskStores = OshiUtil.getDiskStores();
		for (HWDiskStore diskStore : diskStores) {
			List<HWPartition> partitions = diskStore.getPartitions();
			for (HWPartition partition : partitions) {
				log.info("磁盘：{}", partition.getMountPoint());
				log.info("容量：{}", FormatUtil.formatBytes(partition.getSize()));
			}
		}
	}

	/**
	 * JavaInfo
	 */
	@Test
	public void javaInfo() {
		JavaInfo javaInfo = SystemUtil.getJavaInfo();
		log.info("Java 运行版本：{}", javaInfo.getVersion());
	}

	/**
	 * JvmInfo
	 */
	@Test
	public void JvmInfo() {
		log.info("Jvm 内存总量：{}", FormatUtil.formatBytes(SystemUtil.getTotalMemory()));
		log.info("Jvm 已使用内存：{}", FormatUtil.formatBytes(SystemUtil.getTotalMemory() - SystemUtil.getFreeMemory()));
	}
}