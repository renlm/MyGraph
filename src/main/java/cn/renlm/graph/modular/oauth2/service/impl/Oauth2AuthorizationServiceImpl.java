package cn.renlm.graph.modular.oauth2.service.impl;

import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import cn.renlm.graph.modular.oauth2.entity.Oauth2Authorization;
import cn.renlm.graph.modular.oauth2.mapper.Oauth2AuthorizationMapper;
import cn.renlm.graph.modular.oauth2.service.IOauth2AuthorizationService;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author Renlm
 * @since 2023-01-07
 */
@Service
public class Oauth2AuthorizationServiceImpl extends ServiceImpl<Oauth2AuthorizationMapper, Oauth2Authorization> implements IOauth2AuthorizationService {

}
