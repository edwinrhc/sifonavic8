package org.st.gob.pe.sifonavic8.controller;

import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;

public class GlobalErrorController implements ErrorController {
    @RequestMapping("/error")
    public String handleError(HttpServletRequest request) {
        // Obtén el código de estado de la respuesta
        Integer statusCode = (Integer) request.getAttribute("javax.servlet.error.status_code");

        if (statusCode != null) {
            if(statusCode == 404) {
                return "error/404";
            }
            else if(statusCode == 403) {
                return "error/403";
            }
            // Maneja otros códigos de error si es necesario
        }
        // Página de error genérica
        return "error/error";
    }
}
