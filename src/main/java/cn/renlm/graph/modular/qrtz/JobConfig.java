package cn.renlm.graph.modular.qrtz;

import java.io.Serializable;
import java.util.List;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import com.thoughtworks.xstream.annotations.XStreamConverter;
import com.thoughtworks.xstream.annotations.XStreamImplicit;
import com.thoughtworks.xstream.converters.extended.ToAttributedValueConverter;

import lombok.Data;

/**
 * 
 * 任务配置类
 * 
 * @author RenLiMing(任黎明)
 *
 */
@Data
@XStreamAlias("jobs")
public class JobConfig implements Serializable {

	private static final long serialVersionUID = 1L;

	@XStreamImplicit(itemFieldName = "job")
	private List<JobItem> jobs;

	@Data
	@XStreamConverter(value = ToAttributedValueConverter.class, strings = { "description" })
	public static final class JobItem implements Serializable {

		private static final long serialVersionUID = 1L;

		@XStreamAsAttribute
		@XStreamAlias("className")
		private String jobClassName;

		private String description;

	}
}