package org.st.gob.pe.sifonavic8.component;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class CustomAuthenticationSuccessHandler  extends SavedRequestAwareAuthenticationSuccessHandler {

    @Autowired
    private BaseUrlResolver baseUrlResolver;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws ServletException, IOException {
        String targetUrl = determineTargetUrl(request, response);

        // Construir la URL de redirección completa usando la URL base externa
        String redirectUrl = baseUrlResolver.buildUrl(targetUrl);

        // Redirigir al usuario
        getRedirectStrategy().sendRedirect(request, response, redirectUrl);
    }
}