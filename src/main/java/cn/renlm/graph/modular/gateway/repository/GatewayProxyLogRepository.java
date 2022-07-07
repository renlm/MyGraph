package cn.renlm.graph.modular.gateway.repository;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import cn.renlm.graph.modular.gateway.doc.GatewayProxyLogDOC;

/**
 * 网关代理日志
 * 
 * @author Renlm
 *
 */
public interface GatewayProxyLogRepository extends ElasticsearchRepository<GatewayProxyLogDOC, Long> {

}