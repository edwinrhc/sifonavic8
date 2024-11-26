package org.st.gob.pe.sifonavic8.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;

import javax.sql.DataSource;


@Configuration
@EnableWebSecurity
public class DatabaseWebSecurity {


    @Bean
    public UserDetailsService usersCustom(DataSource dataSource) {

        JdbcUserDetailsManager users = new JdbcUserDetailsManager(dataSource);
        // Configuración de consulta de usuarios
        users.setUsersByUsernameQuery(
                "SELECT u.USC_D_USUARIO, b.USC_D_PASSWORD_BCRYPT, u.USC_E_REGISTRO " +
                        "FROM FONAVI.GSEC_USUARIO u " +
                        "INNER JOIN FONAVI.GSEC_USUARIO_BCRYPT b ON u.USC_C_USUARIO = b.USC_C_USUARIO " +
                        "WHERE u.USC_D_USUARIO = ?"
        );

        // Configuración de consulta de roles
        users.setAuthoritiesByUsernameQuery(
                "SELECT u.USC_D_USUARIO, r.ROL_D_DESCRIPCION " +
                        "FROM FONAVI.GSEC_USUARIO_ROL_BCRYPT ur " +
                        "INNER JOIN FONAVI.GSEC_USUARIO u ON u.USC_C_USUARIO = ur.USC_C_USUARIO " +
                        "INNER JOIN FONAVI.GSEC_ROL_BCRYPT r ON r.ROL_C_ID = ur.ROL_C_ID " +
                        "WHERE u.USC_D_USUARIO = ?"
        );

        return users;
    }




    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf()
//                .disable()
                .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
                .and()
                .authorizeRequests()
                .antMatchers(  "/css/**","/error","/error/**","/captcha").permitAll()
                .anyRequest().authenticated()
                .and()
                .httpBasic()
                .and()
                .formLogin()
                    .loginPage("/login")
                    .permitAll()
                .and()
                    .logout().permitAll()
                .and()
                .exceptionHandling().accessDeniedPage("/error_403");

        return http.build();
    }


    // Registrar AuthenticationManager como un bean
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

}

