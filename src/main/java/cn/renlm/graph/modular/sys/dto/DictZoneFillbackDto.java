package cn.renlm.graph.modular.sys.dto;

import java.io.Serializable;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 区域字典填充
 * 
 * @author Renlm
 *
 */
@Data
@Accessors(chain = true)
public class DictZoneFillbackDto implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * 国家编码
	 */
	private String countryCode;

	/**
	 * 国家名称
	 */
	private String countryName;

	/**
	 * 省编码
	 */
	private String provinceCode;

	/**
	 * 省名称
	 */
	private String provinceName;

	/**
	 * 市编码
	 */
	private String cityCode;

	/**
	 * 市名称
	 */
	private String cityName;

	/**
	 * 区县编码
	 */
	private String districtCode;

	/**
	 * 区县名称
	 */
	private String districtName;

}