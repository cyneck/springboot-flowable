package com.cyneck.workflow.config;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Resource;

/**
 * <p>Description: </p>
 *
 * @author Eric Lee
 * @version v1.0.0
 * @since 2021/2/18 23:02
 **/

@Configuration
public class FilterConfig {

    @Resource
    UserFilter userFilter;

    @Bean
    public FilterRegistrationBean myFilter() {
        final FilterRegistrationBean registrationBean = new FilterRegistrationBean();
        registrationBean.setFilter(userFilter);
        return registrationBean;
    }
}
