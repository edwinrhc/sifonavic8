package org.st.gob.pe.sifonavic8.controller.api;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.st.gob.pe.sifonavic8.common.FileProcessingResult;
import org.st.gob.pe.sifonavic8.dto.CargaInsertHerederoDTO;
import org.st.gob.pe.sifonavic8.entity.CargaPrevia;
import org.st.gob.pe.sifonavic8.mapper.CargaPreviaMapper;
import org.st.gob.pe.sifonavic8.service.CargaPreviaService;
import org.st.gob.pe.sifonavic8.service.FileUploadService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
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

    @Autowired
    private HttpSession  httpSession;

    private final FileUploadService fileUploadService;

    public CargaCSVRestController(FileUploadService fileUploadService) {
        this.fileUploadService = fileUploadService;
    }

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




/*    @PostMapping("/cargaInsertDataHedereros")
    public ResponseEntity<?> handleFileUploadInsertDataHerederos(
            @RequestParam("file") MultipartFile file,
            HttpServletRequest request
    ) {
        logger.info("Petición recibida para cargar archivo");

        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("message", "El archivo está vacío"));
        }

        String usuarioActual = obtenerUsuarioActual(request);
        if (usuarioActual == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "Usuario no autenticado. Por favor, inicie sesión"));
        }

        FileProcessingResult result = fileUploadService.processFileInsertHeredero(file, usuarioActual);
        if (result.hasErrors()) {
            return ResponseEntity.badRequest()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(result.getErrorResponse());
        }

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(Map.of(
                        "message", result.getSuccessResponse().get("message"),
                        "fileName", result.getProcessedFileName(),
                        "fileUrl", "/api/v1/files/" + result.getProcessedFileName()
                ));

    }*/



    @PostMapping("/cargaInsertDataHedereros")
    public ResponseEntity<?> handleFileUploadInsertDataHerederos(
            @RequestParam("file") MultipartFile file,
            HttpServletRequest request
    ) {
        logger.info("Petición recibida para cargar archivo");

        if (file.isEmpty() || file.getSize() == 0) {
            return ResponseEntity.badRequest().body(Map.of(
                    "status", "error",
                    "message", "El archivo está vacío o no tiene contenido."
            ));
        }

        String usuarioActual = obtenerUsuarioActual(request);
        if (usuarioActual == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of(
                            "status", "error",
                            "message", "Usuario no autenticado. Por favor, inicie sesión"
                    ));
        }

        FileProcessingResult result = fileUploadService.processFileInsertHeredero(file, usuarioActual);
        if (result.hasErrors()) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("status", "error");
            errorResponse.put("message", result.getErrorResponse().get("message"));
            errorResponse.put("errors", result.getErrorResponse().get("errors")); // Agregar detalles de los errores

            return ResponseEntity.badRequest()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(errorResponse);
        }

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(Map.of(
                        "status", "success",
                        "message", result.getSuccessResponse().get("message")
                ));
    }



    @PostMapping("/cargaActualizarFechaHedereros")
    public ResponseEntity<?> handleFileUploadUpdateDateHerederos(
            @RequestParam("file") MultipartFile file,
            HttpServletRequest request
    ){
        logger.info("Petición recibida para cargar archivo update date");

        if(file.isEmpty()|| file.getSize() == 0){
            return ResponseEntity.badRequest().body(Map.of(
               "status","error",
               "message","El archivo está vacío no tiene contenido."
            ));
        }
        String usuarioActual = obtenerUsuarioActual(request);
        if(usuarioActual == null){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of(
                            "status","error",
                            "message","Usuario no autenticado. Por favor, inicie sesión"
                    ));
        }

        FileProcessingResult result = fileUploadService.processFileFechaFallecido(file,usuarioActual);
        if(result.hasErrors()){
            Map<String,String> errorResponse = new HashMap<>();
            errorResponse.put("status","error");
            errorResponse.put("message",result.getErrorResponse().get("message"));
            errorResponse.put("errors",result.getErrorResponse().get("errors")); // Agregar detalles de los errores

            return ResponseEntity.badRequest()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(errorResponse);
        }
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(Map.of(
                        "status","success",
                        "message",result.getSuccessResponse().get("message")
                ));
    }


    @PostMapping("/cargaActualizarHerederos")
    public ResponseEntity<?> handleFileUploadUpdateHerederos(
            @RequestParam("file") MultipartFile file,
            HttpServletRequest request
    ){
        logger.info("Petición recibida para cargar archivo update");
        if(file.isEmpty()|| file.getSize()==0){
            return ResponseEntity.badRequest().body(Map.of(
               "status","error",
               "message","El archivo está vacío no tiene contenido."
            ));
        }
        String usuarioActual = obtenerUsuarioActual(request);
        if(usuarioActual == null){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of(
                            "status","error",
                            "message","Usuario no autenticado. Por favor, inicie sesión"
                    ));
        }

        FileProcessingResult result = fileUploadService.processFileActualizarHeredero(file,usuarioActual);
        if(result.hasErrors()){
            Map<String,String> errorResponse = new HashMap<>();
            errorResponse.put("status","error");
            errorResponse.put("message",result.getErrorResponse().get("message"));
            errorResponse.put("errors",result.getErrorResponse().get("errors"));

            return ResponseEntity.badRequest()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(errorResponse);
        }
        return  ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(Map.of(
                        "status","success",
                        "message",result.getSuccessResponse().get("message")
                ));
    }



    @GetMapping("/descargarArchivo")
    public ResponseEntity<Resource> descargarArchivo(@RequestParam String fileName) throws IOException {
        Path filePath = Paths.get(fileName);
        if (Files.exists(filePath)) {
            Resource resource = new InputStreamResource(Files.newInputStream(filePath));
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + fileName)
                    .contentType(MediaType.TEXT_PLAIN)
                    .body(resource);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }

    }

    private String obtenerUsuarioActual(HttpServletRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated() && !(authentication instanceof AnonymousAuthenticationToken)) {
            return authentication.getName();
        }

        return null;
    }








}
