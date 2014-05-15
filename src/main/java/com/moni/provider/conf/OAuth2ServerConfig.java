
package com.moni.provider.conf;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.approval.ApprovalStore;
import org.springframework.security.oauth2.provider.approval.TokenApprovalStore;
import org.springframework.security.oauth2.provider.approval.UserApprovalHandler;
import org.springframework.security.oauth2.provider.request.DefaultOAuth2RequestFactory;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.InMemoryTokenStore;
import org.springframework.security.oauth2.provider.authentication.OAuth2AuthenticationManager;

import java.util.logging.Logger;


@Configuration
public class OAuth2ServerConfig {

    private static final Logger logger = Logger.getLogger(OAuth2ServerConfig.class.getName());

    // not a significant name
	private static final String RESOURCE_ID = "dmAPI";

	@Configuration
	@Order(10)
	protected static class UiResourceConfiguration extends WebSecurityConfigurerAdapter
    {
		@Override
		protected void configure(HttpSecurity http) throws Exception
        {
            logger.info(">>> 0 inside Uiresourceconfiguration.configure(...)");

			http
			 	.requestMatchers().antMatchers("/api/greeting/**")
			.and()			   
				.authorizeRequests()
	            .antMatchers("/api/greeting").access("hasRole('USER')")
	            .antMatchers("/api/greeting/**").access("hasRole('USER')")
			;
		}
	}

	@Configuration
	@EnableResourceServer
	protected static class ResourceServerConfiguration extends ResourceServerConfigurerAdapter {

		@Override
		public void configure(ResourceServerSecurityConfigurer resources)
        {
            logger.info(">>> 1 inside ResourceServerConfiguration.configure(...)");

            resources.resourceId(RESOURCE_ID);
		}

		@Override
		public void configure(HttpSecurity http) throws Exception
        {
            logger.info(">>> 2 inside ResourceServerConfiguration.configure(...)");

			http
				.requestMatchers().antMatchers("/api/greeting/**", "/oauth/users/**", "/oauth/clients/**")
			.and()
				.authorizeRequests()					
					.antMatchers("/api/greeting").access("#oauth2.hasScope('read')")
					.antMatchers("/api/greeting").access("#oauth2.hasScope('trust')")
					.antMatchers("/api/greeting/**").access("#oauth2.hasScope('read')")
					.antMatchers("/api/greeting/**").access("#oauth2.hasScope('trust')")
					.regexMatchers(HttpMethod.DELETE, "/platform/oauth/users/([^/].*?)/tokens/.*")
						.access("#oauth2.clientHasRole('ROLE_CLIENT') and (hasRole('ROLE_USER') or #oauth2.isClient()) and #oauth2.hasScope('write')")
					.regexMatchers(HttpMethod.GET, "/platform/oauth/clients/([^/].*?)/users/.*")
						.access("#oauth2.clientHasRole('ROLE_CLIENT') and (hasRole('ROLE_USER') or #oauth2.isClient()) and #oauth2.hasScope('read')")
					.regexMatchers(HttpMethod.GET, "/platform/oauth/clients/.*")
						.access("#oauth2.clientHasRole('ROLE_CLIENT') and #oauth2.isClient() and #oauth2.hasScope('read')");
		}

	}

	@Configuration
	@EnableAuthorizationServer
	protected static class AuthorizationServerConfiguration extends AuthorizationServerConfigurerAdapter
    {

		@Autowired
		private TokenStore tokenStore;
		
		@Autowired
		@Qualifier("authenticationManagerBean")
		private AuthenticationManager authenticationManager;

		// we do not need this. keep it for now for readablility of other flows
		@Value("${myapp.redirect:http://localhost:8080/platform/redirect}")
		private String myAppRedirectUri;
		
		@Override
		public void configure(ClientDetailsServiceConfigurer clients) throws Exception
        {
            logger.info(">>> 3 inside AuthorizationServerConfiguration.configure(...)");
		
			clients.inMemory().withClient("myapp")
			 			.resourceIds(RESOURCE_ID)
			 			.authorizedGrantTypes("authorization_code", "implicit")
			 			.authorities("ROLE_CLIENT")
			 			.scopes("read", "write")
			 			.secret("secret")
			 		.and()
			 		.withClient("myapp-with-redirect")
			 			.resourceIds(RESOURCE_ID)
			 			.authorizedGrantTypes("authorization_code", "implicit")
			 			.authorities("ROLE_CLIENT")
			 			.scopes("read", "write")
			 			.secret("secret")
			 			.redirectUris(myAppRedirectUri)
			 		.and()
		 		    .withClient("my-client-with-registered-redirect")
	 			        .resourceIds(RESOURCE_ID)
	 			        .authorizedGrantTypes("authorization_code", "client_credentials")
	 			        .authorities("ROLE_CLIENT")
	 			        .scopes("read", "trust")
	 			        .redirectUris("http://anywhere?key=value")
		 		    .and()
	 		        .withClient("my-trusted-client")
 			            .authorizedGrantTypes("password", "authorization_code", "refresh_token", "implicit")
 			            .authorities("ROLE_CLIENT", "ROLE_TRUSTED_CLIENT")
 			            .scopes("read", "write", "trust")
 			            .accessTokenValiditySeconds(60)
		 		    .and()
	 		        .withClient("my-trusted-client-with-secret")
 			            .authorizedGrantTypes("password", "authorization_code", "refresh_token", "implicit", "client_credentials")
 			            .authorities("ROLE_CLIENT", "ROLE_TRUSTED_CLIENT")
 			            .scopes("read", "write", "trust")
 			            .secret("somesecret")
	 		        .and()
 		            .withClient("my-less-trusted-client")
			            .authorizedGrantTypes("authorization_code", "implicit")
			            .authorities("ROLE_CLIENT")
			            .scopes("read", "write", "trust")
     		        .and()
		            .withClient("my-less-trusted-autoapprove-client")
		                .authorizedGrantTypes("implicit")
		                .authorities("ROLE_CLIENT")
		                .scopes("read", "write", "trust")
		                .autoApprove(true);			
		}

		@Bean
		public TokenStore tokenStore() {
            logger.info(">>> 4 inside AuthorizationServerConfiguration.tokenStore()");

            return new InMemoryTokenStore();
		}

		@Override
		public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception
        {
            logger.info(">>> 5 inside AuthorizationServerConfiguration.configure(...)");

			endpoints.tokenStore(tokenStore)
					.authenticationManager(authenticationManager);
		}

		@Override
		public void configure(AuthorizationServerSecurityConfigurer oauthServer) throws Exception
        {
            logger.info(">>> 6 inside AuthorizationServerConfiguration.configure(...)");

			oauthServer.realm("myApp/client");
		}

	}
	
	protected static class Stuff {
	
		@Autowired
		private ClientDetailsService clientDetailsService;

		@Autowired
		private TokenStore tokenStore;

		@Bean
		public ApprovalStore approvalStore() throws Exception
        {
            logger.info(">>> 7 inside Stuff.approvalStore()");

			TokenApprovalStore store = new TokenApprovalStore();
			store.setTokenStore(tokenStore);
			return store;
		}

	}

}
