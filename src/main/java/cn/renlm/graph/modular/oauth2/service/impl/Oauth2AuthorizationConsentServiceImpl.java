package cn.renlm.graph.modular.oauth2.service.impl;

import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import cn.renlm.graph.modular.oauth2.entity.Oauth2AuthorizationConsent;
import cn.renlm.graph.modular.oauth2.mapper.Oauth2AuthorizationConsentMapper;
import cn.renlm.graph.modular.oauth2.service.IOauth2AuthorizationConsentService;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author Renlm
 * @since 2023-01-07
 */
@Service
public class Oauth2AuthorizationConsentServiceImpl extends ServiceImpl<Oauth2AuthorizationConsentMapper, Oauth2AuthorizationConsent> implements IOauth2AuthorizationConsentService {

}
