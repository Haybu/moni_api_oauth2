package com.moni.provider.conf;

import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {   
	
	    private static final Logger logger = Logger.getLogger(SecurityConfiguration.class.getName());

        // in memory authentication
        @Override
        protected void configure(AuthenticationManagerBuilder auth) throws Exception
        {
        	logger.info(">>> A1 Configuring API authentication manager");

            auth
                .inMemoryAuthentication()
                	.withUser("user").password("password").roles("USER")
                .and()
                	.withUser("admin").password("password").roles("USER", "ADMIN")
                ;
        }

        @Override
        @Bean
        public AuthenticationManager authenticationManagerBean() throws Exception {
            return super.authenticationManagerBean();
        }

        // authorization
		@Override
        protected void configure(HttpSecurity http) throws Exception 
        {
        	logger.info(">>> A2 Configuring API authorization security");
        	
            http
            	.authorizeRequests().antMatchers("/platform/autoconfig").permitAll()
            .and()
                .authorizeRequests().anyRequest().hasRole("USER")
             .and()
	            .csrf().requireCsrfProtectionMatcher(new AntPathRequestMatcher("/oauth/authorize")).disable()
	            .httpBasic();	            	     
        }

}
