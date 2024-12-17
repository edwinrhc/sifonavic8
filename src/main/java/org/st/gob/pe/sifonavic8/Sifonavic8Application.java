package org.st.gob.pe.sifonavic8;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.web.filter.ForwardedHeaderFilter;

import javax.servlet.http.HttpServletRequest;

@SpringBootApplication
@MapperScan("org.st.gob.pe.sifonavic8.mapper")
public class Sifonavic8Application extends SpringBootServletInitializer {

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(Sifonavic8Application.class);
    }

    public static void main(String[] args) {
        SpringApplication.run(Sifonavic8Application.class, args);
    }




    @Bean
    public FilterRegistrationBean filterLogging() {
        FilterRegistrationBean<javax.servlet.Filter> registration = new FilterRegistrationBean<>();
        registration.setFilter((request, response, chain) -> {
            HttpServletRequest req = (HttpServletRequest) request;
            System.out.println("X-Forwarded-Proto: " + req.getHeader("X-Forwarded-Proto"));
            System.out.println("X-Forwarded-Host: " + req.getHeader("X-Forwarded-Host"));
            chain.doFilter(request, response);
        });
        registration.setOrder(-1);
        return registration;
    }

    @Bean
    public FilterRegistrationBean<ForwardedHeaderFilter> forwardedHeaderFilter() {
        FilterRegistrationBean<ForwardedHeaderFilter> filterRegBean = new FilterRegistrationBean<>();
        filterRegBean.setFilter(new ForwardedHeaderFilter());
        filterRegBean.setOrder(0);
        return filterRegBean;
    }



}
