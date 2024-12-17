package org.st.gob.pe.sifonavic8.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpSession;
import java.security.Principal;
/*
@Controller
public class LoginController {

    private final AuthenticationManager authenticationManager;

    @Autowired
    public LoginController(AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
    }

    @GetMapping({"/","/login"})
    public String login(Model model, @RequestParam(value = "error", required = false) String error) {
        if (error != null) {
            model.addAttribute("error", error);
        }
        return "login";
    }

    @PostMapping("/login")
    public String loginWithCaptcha(@RequestParam("username") String username,
                                   @RequestParam("password") String password,
                                   @RequestParam("captcha") String captcha,
                                   HttpSession session,
                                   RedirectAttributes redirectAttributes) {
        String sessionCaptcha = (String) session.getAttribute("captcha");
        session.removeAttribute("captcha"); // Eliminar el captcha después de usarlo

        if (sessionCaptcha == null || !sessionCaptcha.equalsIgnoreCase(captcha)) {
            redirectAttributes.addFlashAttribute("error", "Captcha incorrecto. Intente nuevamente.");
            return "redirect:/login";
        }

        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(username, password)
            );
            SecurityContextHolder.getContext().setAuthentication(authentication);

            return "redirect:/home";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Nombre de usuario o contraseña incorrectos.");
            return "redirect:/login";
        }
    }
}*/

@Controller
public class LoginController {

    private final AuthenticationManager authenticationManager;

    @Autowired
    public LoginController(AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
    }

 /*   @GetMapping({"/", "/login"})
    public String login(Model model, @RequestParam(value = "error", required = false) String error) {
        if (error != null) {
            model.addAttribute("error", "Nombre de usuario o contraseña incorrectos.");
        }
        return "login"; // Aquí deberías tener tu vista de login
    }

    @PostMapping("/login")
    public String loginWithCaptcha(@RequestParam("username") String username,
                                   @RequestParam("password") String password,
                                   @RequestParam("captcha") String captcha,
                                   HttpSession session,
                                   RedirectAttributes redirectAttributes) {
        String sessionCaptcha = (String) session.getAttribute("captcha");
        session.removeAttribute("captcha"); // Eliminar el captcha después de usarlo

        if (sessionCaptcha == null || !sessionCaptcha.equalsIgnoreCase(captcha)) {
            redirectAttributes.addFlashAttribute("error", "Captcha incorrecto. Intente nuevamente.");
            return "redirect:/login";
        }

        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(username, password)
            );
            SecurityContextHolder.getContext().setAuthentication(authentication);

            return "redirect:/home"; // Redirige al home tras un login exitoso
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Nombre de usuario o contraseña incorrectos.");
            return "redirect:/login";
        }
    }*/


    @GetMapping({"/login"})
    public String login(@RequestParam(value="error", required = false) String error,
                        @RequestParam(value="logout", required = false) String logout,
                        Model model, Principal principal, RedirectAttributes flash
    ) {

        if(principal != null){
            flash.addFlashAttribute("info", "Ya ha iniciado sesión anteriormente");
            return "redirect:/home";
        }

        if(error != null){
            model.addAttribute("error","Error en el login:  Nombre de usuario:  o contraseña incorrecta, por favor vuelva a intentarlo");

        }

        if(logout != null){
            flash.addFlashAttribute("info", "Has cerrado sesion con exito");
            return "redirect:/login"; // Redirige al login después de logout
        }
        return "login"; // Nombre de la plantilla Thymeleaf para el formulario de inicio de sesión
    }

    // Método para manejar el POST a /login
    @PostMapping("/login")
    public String login(@RequestParam("username") String username,
                        @RequestParam("password") String password,
                        RedirectAttributes redirectAttributes) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(username, password)
            );
            SecurityContextHolder.getContext().setAuthentication(authentication);  // Establece el contexto de seguridad
            return "redirect:/home";  // Redirige al home si el login es exitoso
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Nombre de usuario o contraseña incorrectos.");
            return "redirect:/login";  // Redirige a la página de login con un error si la autenticación falla
        }
    }

}

