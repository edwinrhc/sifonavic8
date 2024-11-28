package org.st.gob.pe.sifonavic8.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.http.HttpMethod;
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
                // Recursos públicos
                .antMatchers(  "/css/**","/error","/error/**","/captcha").permitAll()
                // Endpoints específicos
                .antMatchers(HttpMethod.POST, "/api/v1/cargaCSV/cargaInsertDataHedereros").authenticated()
                // Todas las demás rutas requieren autenticación
                .anyRequest().authenticated()
                .and()
                .httpBasic() // Solo si necesitas autenticación básica
                .and()
                .formLogin()
                    .loginPage("/login") // Ruta de la página de inicio de sesión
                    .permitAll()
                .and()
                    .logout()
                        .logoutSuccessUrl("/login?logout") // Redirige tras cerrar sesión
                        .permitAll()
                .and()
                .exceptionHandling()
                    .accessDeniedPage("/error_403");

        return http.build();
    }


    // Registrar AuthenticationManager como un bean
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

}

