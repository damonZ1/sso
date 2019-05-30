package cn.damon.config;

import cn.damon.session.CasFilter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;

@Configuration
public class CasConfig {

    @Bean
    public FilterRegistrationBean sessionFilterRegistration(CasFilter casFilter) {
        FilterRegistrationBean registration = new FilterRegistrationBean();
        registration.setFilter(casFilter);
        registration.addUrlPatterns("/*");
        registration.addInitParameter("paramName", "paramValue");
        registration.setName("casFilter");
        registration.setOrder(1);
        return registration;
    }

    @Bean
    public CasFilter casFilter(RedisTemplate redisTemplate){
        CasFilter sessionFilter = new CasFilter();
        sessionFilter.setRedisTemplate(redisTemplate);
        return sessionFilter;
    }

}
