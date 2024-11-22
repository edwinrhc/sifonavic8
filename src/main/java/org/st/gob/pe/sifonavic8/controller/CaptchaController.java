package org.st.gob.pe.sifonavic8.controller;

import com.google.code.kaptcha.impl.DefaultKaptcha;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.awt.image.BufferedImage;
import java.io.IOException;

@RestController
public class CaptchaController {

    @Autowired
    private DefaultKaptcha captchaProducer;

    @GetMapping("/captcha")
    public void getCaptcha(HttpServletResponse response, HttpSession session) throws IOException {
        // Genera texto del captcha
        String captchaText = captchaProducer.createText();
        // Guarda el texto en la sesión para validarlo más tarde
        session.setAttribute("captcha", captchaText);
        // Configura la respuesta HTTP
        response.setContentType("image/png");
        response.setHeader("Cache-Control", "no-store, no-cache, must-revalidate, max-age=0");
        response.setHeader("Pragma", "no-cache");
        response.setDateHeader("Expires", 0);

        // Genera la imagen del captcha
        BufferedImage captchaImage = captchaProducer.createImage(captchaText);

        // Escribe la imagen al flujo de salida
        ImageIO.write(captchaImage, "png", response.getOutputStream());
    }

}
