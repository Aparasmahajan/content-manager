package com.apex_aura.content_manager.config;

import com.apex_aura.content_manager.filter.JwtRequestFilter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import java.util.Arrays;
import java.util.List;

@Configuration
public class JwtFilterConfig {

    @Value("${jwt.protected-endpoints}")
    private String protectedEndpointsProperty;

    @Bean
    public FilterRegistrationBean<JwtRequestFilter> jwtFilterRegistration(JwtRequestFilter jwtRequestFilter) {
        FilterRegistrationBean<JwtRequestFilter> registrationBean = new FilterRegistrationBean<>();

        registrationBean.setFilter(jwtRequestFilter);
        registrationBean.setOrder(1); // run early in the chain

        // ✅ Define which endpoints require JWT validation
        List<String> protectedEndpoints = Arrays.asList(
                protectedEndpointsProperty.split("\\s*,\\s*")
        );

        registrationBean.setUrlPatterns(protectedEndpoints);

        return registrationBean;
    }
}
