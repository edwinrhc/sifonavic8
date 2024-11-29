package org.st.gob.pe.sifonavic8.configuration;

import com.google.code.kaptcha.impl.DefaultKaptcha;
import com.google.code.kaptcha.util.Config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Properties;

@Configuration
public class CaptchaConfig {

  /*  @Bean
    public DefaultKaptcha getKaptchaBean() {
        DefaultKaptcha defaultKaptcha = new DefaultKaptcha();
        Properties properties = new Properties();
        properties.setProperty("kaptcha.border", "no");
        properties.setProperty("kaptcha.textproducer.font.color", "black");
        properties.setProperty("kaptcha.textproducer.char.space", "5");
        properties.setProperty("kaptcha.image.width", "200");
        properties.setProperty("kaptcha.image.height", "50");
        properties.setProperty("kaptcha.textproducer.font.size", "40");
        defaultKaptcha.setConfig(new Config(properties));
        return defaultKaptcha;
    }*/
  @Bean
  public DefaultKaptcha captchaProducer() {
      DefaultKaptcha defaultKaptcha = new DefaultKaptcha();
      Properties properties = new Properties();
      // Configuración de Kaptcha
      properties.setProperty("kaptcha.border", "no");
      properties.setProperty("kaptcha.textproducer.font.color", "black");
      properties.setProperty("kaptcha.image.width", "200");
      properties.setProperty("kaptcha.image.height", "50");
      properties.setProperty("kaptcha.textproducer.font.size", "40");
      properties.setProperty("kaptcha.session.key", "captcha");
      properties.setProperty("kaptcha.textproducer.char.length", "5");
      properties.setProperty("kaptcha.textproducer.char.string", "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789");
      properties.setProperty("kaptcha.textproducer.font.names", "Arial,Courier");
      Config config = new Config(properties);
      defaultKaptcha.setConfig(config);
      return defaultKaptcha;
  }
}
