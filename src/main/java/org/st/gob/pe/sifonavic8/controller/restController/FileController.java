package org.st.gob.pe.sifonavic8.controller.restController;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class FileController {

    @GetMapping("/download/datos")
    public ResponseEntity<Resource> downloadDatos(){
        Resource file = new ClassPathResource("templates/csv/template_actualizarPersonalesHerederos.csv");
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=template_actualizarPersonalesHerederos.csv")
                .body(file);

    }

    @GetMapping("/download/fecha-fallecida")
    public ResponseEntity<Resource> downloadFechaFallecida(){
        Resource file = new ClassPathResource("templates/csv/template_fecha_fallecido.csv");
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=template_fecha_fallecido.csv")
                .body(file);
    }

    @GetMapping("/download/nuevos-registros")
    public ResponseEntity<Resource> donwloadNuevosRegistros(){
        Resource file = new ClassPathResource("templates/csv/template_insertar_herederos.csv");
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=template_insertar_herederos.csv")
                .body(file);
    }

}
