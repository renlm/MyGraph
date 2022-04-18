package cn.renlm.graph;

import org.junit.jupiter.api.Test;

import cn.hutool.system.oshi.CpuInfo;
import cn.hutool.system.oshi.OshiUtil;
import lombok.extern.slf4j.Slf4j;
import oshi.hardware.GlobalMemory;
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
}