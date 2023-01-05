package cn.renlm.graph.util;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import freemarker.template.Configuration;
import freemarker.template.Template;
import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;

/**
 * 模板工具类
 * 
 * @author Renlm
 *
 */
@UtilityClass
public class FreemarkerUtil {

	protected final static Logger logger = LoggerFactory.getLogger(FreemarkerUtil.class);

	private static final Configuration cfg = new Configuration(Configuration.DEFAULT_INCOMPATIBLE_IMPROVEMENTS);

	static {
		cfg.setClassicCompatible(true);
		cfg.setClassForTemplateLoading(FreemarkerUtil.class, File.separator);
		cfg.setClassLoaderForTemplateLoading(FreemarkerUtil.class.getClassLoader(), File.separator);
	}

	/**
	 * 解析模板
	 * 
	 * @param path
	 * @param value
	 * @return
	 */
	@SneakyThrows
	public final static String read(String path, Object value) {
		Template template = cfg.getTemplate(FilenameUtils.normalize(path, true));
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		template.process(value, new OutputStreamWriter(out));
		return new String(out.toByteArray(), StandardCharsets.UTF_8.name());
	}

	/**
	 * 解析模板
	 * 
	 * @param path
	 * @param key
	 * @param value
	 * @return
	 */
	@SneakyThrows
	public final static String read(String path, String key, Object value) {
		Map<String, Object> dataModel = new HashMap<>();
		dataModel.put(key, value);
		Template template = cfg.getTemplate(FilenameUtils.normalize(path, true));
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		template.process(dataModel, new OutputStreamWriter(out));
		return new String(out.toByteArray(), StandardCharsets.UTF_8.name());
	}

}
