package org.auscope.portal.server.web.security.social;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.social.security.SpringSocialConfigurer;


@Configuration
@EnableWebSecurity
// @ImportResource({"classpath*:applicationContext-security.xml"})
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService);
    }

    /*
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService);
    }
    */

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable();

        // Unsecure pages
        http.authorizeRequests().antMatchers("/", "/signup", "/login", "/logout").permitAll();

        //http.authorizeRequests().antMatchers("/userInfo").access("hasRole('" + AppRole.ROLE_USER + "')");
        http.authorizeRequests().antMatchers("/userInfo").access("hasRole('ROLE_USER')");

        // ADMIN pages
        //http.authorizeRequests().antMatchers("/admin").access("hasRole('" + AppRole.ROLE_ADMIN + "')");
        http.authorizeRequests().antMatchers("/admin").access("hasRole('ROLE_ADMIN')");

        // AccessDeniedException will be thrown if role wrong
        http.authorizeRequests().and().exceptionHandling().accessDeniedPage("/403");

        // Form Login config
        http.authorizeRequests().and().formLogin()
                .loginProcessingUrl("/j_spring_security_check") // Submit URL
                .loginPage("/login")
                .defaultSuccessUrl("/userInfo")
                .failureUrl("/login?error=true")
                .usernameParameter("username")
                .passwordParameter("password");

        // Logout Config
        http.authorizeRequests().and().logout().logoutUrl("/logout").logoutSuccessUrl("/");

        // Spring Social Config.
        http.apply(new SpringSocialConfigurer())
                .signupUrl("/signup");
    }
    
    @Override
    public UserDetailsService userDetailsService() {
        return userDetailsService;
    }

}