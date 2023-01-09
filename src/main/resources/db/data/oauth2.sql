-- OAuth2 注册客户端
DELETE FROM `oauth2_registered_client`;
INSERT INTO `oauth2_registered_client` 
	   (id,client_id,client_id_issued_at,client_secret,client_secret_expires_at,client_name,client_authentication_methods,authorization_grant_types,redirect_uris,scopes,client_settings,token_settings) 
VALUES (uuid(),'otDev',now(),'{noop}secretOtDev',NULL,'opaqueTokenDev','client_secret_post,client_secret_basic','refresh_token,client_credentials,authorization_code,password','http://127.0.0.1:7002/login/oauth2/code/otDev','openid,profile','{"@class":"java.util.Collections$UnmodifiableMap","settings.client.require-proof-key":false,"settings.client.require-authorization-consent":false}','{"@class":"java.util.Collections$UnmodifiableMap","settings.token.reuse-refresh-tokens":true,"settings.token.id-token-signature-algorithm":["org.springframework.security.oauth2.jose.jws.SignatureAlgorithm","RS256"],"settings.token.access-token-time-to-live":["java.time.Duration",300.000000000],"settings.token.access-token-format":{"@class":"org.springframework.security.oauth2.server.authorization.settings.OAuth2TokenFormat","value":"self-contained"},"settings.token.refresh-token-time-to-live":["java.time.Duration",3600.000000000],"settings.token.authorization-code-time-to-live":["java.time.Duration",300.000000000]}')
	  ,(uuid(),'otProd',now(),'{noop}secretOtProd',NULL,'opaqueTokenProd','client_secret_post,client_secret_basic','refresh_token,client_credentials,authorization_code,password','https://gateway.renlm.cn/login/oauth2/code/otProd','openid,profile','{"@class":"java.util.Collections$UnmodifiableMap","settings.client.require-proof-key":false,"settings.client.require-authorization-consent":false}','{"@class":"java.util.Collections$UnmodifiableMap","settings.token.reuse-refresh-tokens":true,"settings.token.id-token-signature-algorithm":["org.springframework.security.oauth2.jose.jws.SignatureAlgorithm","RS256"],"settings.token.access-token-time-to-live":["java.time.Duration",300.000000000],"settings.token.access-token-format":{"@class":"org.springframework.security.oauth2.server.authorization.settings.OAuth2TokenFormat","value":"self-contained"},"settings.token.refresh-token-time-to-live":["java.time.Duration",3600.000000000],"settings.token.authorization-code-time-to-live":["java.time.Duration",300.000000000]}')
;