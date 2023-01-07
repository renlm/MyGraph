package cn.renlm.graph.modular.oauth2.service.impl;

import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import cn.renlm.graph.modular.oauth2.entity.Oauth2RegisteredClient;
import cn.renlm.graph.modular.oauth2.mapper.Oauth2RegisteredClientMapper;
import cn.renlm.graph.modular.oauth2.service.IOauth2RegisteredClientService;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author Renlm
 * @since 2023-01-07
 */
@Service
public class Oauth2RegisteredClientServiceImpl extends ServiceImpl<Oauth2RegisteredClientMapper, Oauth2RegisteredClient> implements IOauth2RegisteredClientService {

}
