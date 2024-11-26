package org.st.gob.pe.sifonavic8.controller;


import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.st.gob.pe.sifonavic8.util.Constantes;

@ControllerAdvice
public class GlobalController {

    @ModelAttribute
    public void addGlobalAttributes(Model model) {
        model.addAttribute("version", Constantes.VERSION);
    }

}
