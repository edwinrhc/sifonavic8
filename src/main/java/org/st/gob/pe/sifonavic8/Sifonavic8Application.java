package org.st.gob.pe.sifonavic8;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

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

}
