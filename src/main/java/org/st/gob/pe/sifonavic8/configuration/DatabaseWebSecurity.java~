package org.st.gob.pe.sifonavic8.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.st.gob.pe.sifonavic8.component.CustomAuthenticationSuccessHandler;

import javax.sql.DataSource;

@Configuration
@EnableWebSecurity
public class DatabaseWebSecurity {

    @Autowired
    private AuthenticationSuccessHandler successHandler;

    @Autowired
    private LogoutSuccessHandler logoutSuccessHandler;


    @Bean
    public UserDetailsService usersCustom(DataSource dataSource) {

        JdbcUserDetailsManager users = new JdbcUserDetailsManager(dataSource);
//        users.setPasswordEncoder(passwordEncoder);
        // Configuración de consulta de usuarios
        users.setUsersByUsernameQuery(
                "SELECT USC_D_USUARIO, USC_D_PASSWORD, USC_E_REGISTRO " +
                        "FROM FONAVI.GSEC_USUARIO " +
                        "WHERE USC_D_USUARIO = ? AND PDD_C_TIPUSR = 427"
        );

        // Configuración de rol estático
        users.setAuthoritiesByUsernameQuery("SELECT ?, 'ROLE_USER' FROM DUAL");

        return users;
    }


    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf().disable()

        //                .requiresChannel()
        //                .anyRequest()
        //                .requiresSecure()
        //                .and()
//                .ignoringAntMatchers("/api/v1/cargaCSV/**")
//                .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
//                .and()
                .authorizeRequests()
//                .antMatchers("/sifonavic8/", "/sifonavic8/login", "/sifonavic8/logout", "/sifonavic8/error", "/sifonavic8/css/**", "/sifonavic8/img/**")
//                .permitAll() // Permitir acceso público a estas rutas
                // Recursos públicos
                .antMatchers(

                        "/img/**",
                        "/public/**",
                        "/css/**",
                        "/error",
                        "/error/**",
                        "/captcha",
                        "/login",
                        "/logout/**",
                        "/download/**",
                        "/api/v1/cargaCSV/**"
                ).permitAll()
                // Endpoints específicos
//                .antMatchers(HttpMethod.POST, "/api/v1/cargaCSV/cargaInsertDataHedereros").authenticated()
                // Todas las demás rutas requieren autenticación
                    .anyRequest()
                    .authenticated()
                .and()
                .formLogin()
//                .loginPage("/sifonavic8/login")  // URL de la página de login
                    .loginPage("/login")  // URL de la página de login
                    .failureUrl("/login?error")  // Redirigir en caso de error
                    .successHandler(successHandler)  // Usar el manejador de éxito personalizado
                    .permitAll()
                 .and()
                    .logout()
                    .logoutUrl("/logout")
                    .logoutSuccessHandler(logoutSuccessHandler)  // Usar el manejador de logout personalizado
                    .permitAll()
                .and()
                .exceptionHandling()
                    .accessDeniedPage("/error/403");

        return http.build();
    }


    // Registrar AuthenticationManager como un bean
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }




}
