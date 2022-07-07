package cn.renlm.graph.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import lombok.Data;

/**
 * Elasticsearch
 * 
 * @author Renlm
 *
 */
@Data
@Component
public class ElasticsearchConfig {

	@Value("${spring.elasticsearch.rest.index-suffix}")
	private String indexSuffix;

}