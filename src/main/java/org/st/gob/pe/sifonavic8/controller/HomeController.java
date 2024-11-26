package org.st.gob.pe.sifonavic8.controller;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.security.Principal;

@Controller
public class HomeController {

    @GetMapping({"/","/home"})
    public String home(Model model, Principal principal, Authentication authentication){
        String usuarioLogeado = ((UserDetails) authentication.getPrincipal()).getUsername();
        String username = principal.getName();
        model.addAttribute("username",username);
        return "home";
    }

}
