package org.st.gob.pe.sifonavic8.controller.api;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.st.gob.pe.sifonavic8.entity.CargaPrevia;
import org.st.gob.pe.sifonavic8.mapper.CargaPreviaMapper;
import org.st.gob.pe.sifonavic8.service.CargaPreviaService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


@RestController()
@RequestMapping("/api/v1/cargaCSV")
public class CargaCSVRestController {


    private static final Logger logger = LoggerFactory.getLogger(CargaCSVRestController.class);

    @Autowired
    public CargaPreviaMapper cargaPreviaMapper;

    @Autowired
    public CargaPreviaService cargaPreviaService;

    @GetMapping("/search")
    public ResponseEntity<Map<String,Object>> getMostrar(
            @RequestParam(required = false) String tipoDocumento,
            @RequestParam(required = false) String numDocumento
    ){
        List<CargaPrevia> consultaPersona = cargaPreviaService.consultaPersona(tipoDocumento,numDocumento);

        Map<String,Object> response = new HashMap<>();
        response.put("consultaPersona", consultaPersona);
        return ResponseEntity.ok(response);
    }








}