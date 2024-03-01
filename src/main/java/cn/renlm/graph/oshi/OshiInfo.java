package cn.renlm.graph.oshi;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 服务器信息
 * 
 * @author RenLiMing(任黎明)
 *
 */
@Data
@Accessors(chain = true)
public class OshiInfo implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * 机器ip
	 */
	private String ip;

	/**
	 * 采集时间
	 */
	private Date time;

	/**
	 * 唯一标识
	 */
	private Long uid;

	/**
	 * cpu 核心数
	 */
	private Integer cpuCores;

	/**
	 * cpu 使用率（%）
	 */
	private BigDecimal cpuUsedRate;

	/**
	 * 系统内存（字节）
	 */
	private Long memory;

	/**
	 * 系统内存（格式化字符）
	 */
	public String memoryStr;

	/**
	 * 已使用系统内存（字节）
	 */
	private Long memoryUsed;

	/**
	 * 已使用系统内存（格式化字符）
	 */
	public String memoryUsedStr;

	/**
	 * 系统内存使用率（%）
	 */
	private BigDecimal memoryUsedRate;

	/**
	 * 磁盘（字节）
	 */
	private Long disk;

	/**
	 * 磁盘（格式化字符）
	 */
	public String diskStr;

	/**
	 * 已使用磁盘（字节）
	 */
	private Long diskUsed;

	/**
	 * 已使用磁盘（格式化字符）
	 */
	public String diskUsedStr;

	/**
	 * 磁盘使用率（%）
	 */
	private BigDecimal diskUsedRate;

	/**
	 * 操作系统
	 */
	private String osName;

	/**
	 * 系统架构
	 */
	private String osArch;

	/**
	 * jvm 内存总量（字节）
	 */
	private Long jvmMemory;

	/**
	 * jvm 内存总量（格式化字符）
	 */
	public String jvmMemoryStr;

	/**
	 * jvm 已使用内存（字节）
	 */
	private Long jvmMemoryUsed;

	/**
	 * jvm 已使用内存（格式化字符）
	 */
	public String jvmMemoryUsedStr;

	/**
	 * jvm 内存使用率（%）
	 */
	private BigDecimal jvmMemoryUsedRate;

	/**
	 * java 运行版本
	 */
	private String javaVersion;

}