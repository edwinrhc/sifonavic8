package org.st.gob.pe.sifonavic8;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("org.st.gob.pe.sifonavic8.mapper")
public class Sifonavic8Application {

    public static void main(String[] args) {
        SpringApplication.run(Sifonavic8Application.class, args);
    }

}
