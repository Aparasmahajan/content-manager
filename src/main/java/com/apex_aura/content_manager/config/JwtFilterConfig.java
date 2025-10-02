package com.apex_aura.content_manager.config;

import com.apex_aura.content_manager.filter.JwtRequestFilter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import java.util.Arrays;

@Configuration
public class JwtFilterConfig {

    @Bean
    public FilterRegistrationBean<JwtRequestFilter> jwtFilterRegistration(JwtRequestFilter jwtRequestFilter) {
        FilterRegistrationBean<JwtRequestFilter> registrationBean = new FilterRegistrationBean<>();

        registrationBean.setFilter(jwtRequestFilter);
        registrationBean.setOrder(1); // run early in the chain

        registrationBean.setUrlPatterns(Arrays.asList("/*"));



        return registrationBean;
    }
}
