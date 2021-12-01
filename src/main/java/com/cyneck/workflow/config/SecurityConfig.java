package com.cyneck.workflow.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.stereotype.Component;

/**
 * <p>Description: </p>
 *
 * @author Eric Lee
 * @version v1.0.0
 * @since 2021/1/21 12:26
 **/

@Component
@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    //Http安全配置，对每个到达系统的http请求链接进行校验
    @Override
    public void configure(HttpSecurity http) throws Exception {
        //所有请求必须认证通过
        http.headers().cacheControl().disable();
        http.httpBasic()
                .and()
                .authorizeRequests()
                .antMatchers(
                        "/error",
                        "/**/js/**",
                        "/**/imgs/**",
                        "/**/css/**",
                        "/webjars/**",
                        "swagger-ui.html",
                        "**/swagger-ui.html",
                        "/swagger-resources/**",
                        "/v2/api-docs",
                        "/swagger-resources/**",
                        "/**/*.ttf",
                        "/doc.html")
                .permitAll()
                .anyRequest()
                .authenticated();


    }

}
