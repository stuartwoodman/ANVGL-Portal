package org.auscope.portal.server.web.security.social;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.Filter;

import org.auscope.portal.server.web.security.RedirectUnconfiguredUserHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.oauth2.resource.ResourceServerProperties;
import org.springframework.boot.autoconfigure.security.oauth2.resource.UserInfoTokenServices;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.oauth2.client.OAuth2ClientContext;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;
import org.springframework.security.oauth2.client.filter.OAuth2ClientAuthenticationProcessingFilter;
import org.springframework.security.oauth2.client.filter.OAuth2ClientContextFilter;
import org.springframework.security.oauth2.client.token.grant.code.AuthorizationCodeResourceDetails;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableOAuth2Client;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.web.filter.CompositeFilter;


/**
 * 
 * Basis for token based social authentication, based on Spring Tutorial:
 * https://spring.io/guides/tutorials/spring-boot-oauth2/
 * 
 * Note that the Authorisation Server code was not tested, so has been commented out
 * 
 * @author woo392
 *
 */
@Configuration
@EnableOAuth2Client
//@EnableAuthorizationServer
@PropertySource("classpath:social-cfg.properties")
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    OAuth2ClientContext oauth2ClientContext;
    
    @Autowired
    private RedirectUnconfiguredUserHandler redirectUnconfiguredUserHandler;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        
        http.authorizeRequests()
        
        .anyRequest().hasAnyRole("ADMIN", "USER")
        .and()
        
        .authorizeRequests().antMatchers("/login**").permitAll()
        .and()
        
        .logout().logoutSuccessUrl("http://localhost:4200/").permitAll()
        .and()
        
        .addFilterBefore(ssoFilter(), BasicAuthenticationFilter.class)
        
        .csrf().disable();
    }
    
    private Filter ssoFilter() {
        CompositeFilter filter = new CompositeFilter();
        List<Filter> filters = new ArrayList<>();
        filters.add(ssoFilter(facebook(), "/login/facebook"));
        filters.add(ssoFilter(github(), "/login/github"));
        filter.setFilters(filters);
        return filter;
    }

    private Filter ssoFilter(ClientResources client, String path) {
        OAuth2ClientAuthenticationProcessingFilter filter = new OAuth2ClientAuthenticationProcessingFilter(path);
        OAuth2RestTemplate template = new OAuth2RestTemplate(client.getClient(), oauth2ClientContext);
        filter.setRestTemplate(template);
        UserInfoTokenServices tokenServices = new UserInfoTokenServices(client.getResource().getUserInfoUri(),
                client.getClient().getClientId());
        tokenServices.setRestTemplate(template);
        filter.setTokenServices(tokenServices);

        filter.setAuthenticationSuccessHandler(redirectUnconfiguredUserHandler);
        
        return filter;
    }

    @Bean
    @ConfigurationProperties("facebook")
    public ClientResources facebook() {
      return new ClientResources();
    }

    @Bean
    @ConfigurationProperties("github")
    public ClientResources github() {
      return new ClientResources();
    }

    @Bean
    public FilterRegistrationBean oauth2ClientFilterRegistration(OAuth2ClientContextFilter filter) {
        FilterRegistrationBean registration = new FilterRegistrationBean();
        registration.setFilter(filter);
        registration.setOrder(-100);
        return registration;
    }

    /**
     * 
     * @author woo392
     *
     */
    class ClientResources {

        @NestedConfigurationProperty
        private AuthorizationCodeResourceDetails client = new AuthorizationCodeResourceDetails();

        @NestedConfigurationProperty
        private ResourceServerProperties resource = new ResourceServerProperties();

        public AuthorizationCodeResourceDetails getClient() {
            return client;
        }

        public ResourceServerProperties getResource() {
            return resource;
        }
    }
}
