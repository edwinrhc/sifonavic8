package org.st.gob.pe.sifonavic8.configuration;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.crypto.password.PasswordEncoder;

import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.web.filter.ForwardedHeaderFilter;

import javax.sql.DataSource;

@Configuration
@EnableWebSecurity
public class DatabaseWebSecurity {


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

        // Configuración de consulta de roles
/*        users.setAuthoritiesByUsernameQuery(
                "SELECT u.USC_D_USUARIO, r.ROL_D_DESCRIPCION " +
                        "FROM FONAVI.GSEC_USUARIO_ROL_BCRYPT ur " +
                        "INNER JOIN FONAVI.GSEC_USUARIO u ON u.USC_C_USUARIO = ur.USC_C_USUARIO " +
                        "INNER JOIN FONAVI.GSEC_ROL_BCRYPT r ON r.ROL_C_ID = ur.ROL_C_ID " +
                        "WHERE u.USC_D_USUARIO = ?"
        );*/
        // Configuración de rol estático
        users.setAuthoritiesByUsernameQuery("SELECT ?, 'ROLE_USER' FROM DUAL");

        return users;
    }

//    @Bean
//    public AuthenticationManager authenticationManager(DataSource dataSource, PasswordEncoder passwordEncoder) {
//        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
//        authProvider.setUserDetailsService(usersCustom(dataSource));
//        authProvider.setPasswordEncoder(passwordEncoder);
//
//        return new ProviderManager(authProvider);
//    }




    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf()
                .ignoringAntMatchers("/api/v1/cargaCSV/**")
                .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
                .and()
                .authorizeRequests()
                // Recursos públicos
                .antMatchers(
                        "/",
                        "/img/**",
                        "/public/**",
                        "/css/**",
                        "/error",
                        "/error/**",
                        "/captcha",
                        "/login",
                        "/logout",
                        "/download/**",
                        "/api/v1/cargaCSV/**"
                ).permitAll()
                // Endpoints específicos
//                .antMatchers(HttpMethod.POST, "/api/v1/cargaCSV/cargaInsertDataHedereros").authenticated()
                // Todas las demás rutas requieren autenticación
                .anyRequest().authenticated()
                .and()
                .formLogin().loginPage("/login")
                .permitAll()
                // Elimina o comenta estas líneas:
                // .httpBasic() // Solo si necesitas autenticación básica
                 .and()
                // .formLogin()
                //     .loginPage("/login") // Ruta de la página de inicio de sesión
                //     .permitAll()
                // .and()
                // .logout()
                //     .logoutSuccessUrl("/login?logout") // Redirige tras cerrar sesión
                //     .permitAll()
                // .and()
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
