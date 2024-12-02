package org.st.gob.pe.sifonavic8.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.DelegatingPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class PasswordEncoderConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        String idForEncode = "MD5"; // Usar MD5 como algoritmo por defecto
        Map<String, PasswordEncoder> encoders = new HashMap<>();
        encoders.put("MD5", new MD5PasswordEncoder());
        // Puedes incluir bcrypt si lo deseas para futuros usos
        encoders.put("bcrypt", new BCryptPasswordEncoder());

        DelegatingPasswordEncoder delegatingPasswordEncoder = new DelegatingPasswordEncoder(idForEncode, encoders);
        // Establecer el PasswordEncoder por defecto para contraseñas sin prefijo
        delegatingPasswordEncoder.setDefaultPasswordEncoderForMatches(new MD5PasswordEncoder());

        return delegatingPasswordEncoder;
    }


}
