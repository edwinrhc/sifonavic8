package org.st.gob.pe.sifonavic8.controller;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.st.gob.pe.sifonavic8.entity.CargaPrevia;
import org.st.gob.pe.sifonavic8.mapper.CargaPreviaMapper;
import org.st.gob.pe.sifonavic8.service.CargaPreviaService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Controller()
@RequestMapping("/cargaCSV")
public class CargaCSVController {

    @GetMapping("/search")
    public String getMostrar(){
        return "view/consultaPersona";
    }

    @GetMapping("/insert")
    public String getInsert(){
        return "view/insertarPersona";
    }

    @GetMapping("/update")
    public String getUpdate(){
        return "view/updatePersona";
    }

    @GetMapping("/updateDeath")
    public String getupdateDeath(){
        return "view/updateDeathPersona";
    }

}

