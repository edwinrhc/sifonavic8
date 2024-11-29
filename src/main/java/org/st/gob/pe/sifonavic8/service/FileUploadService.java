package org.st.gob.pe.sifonavic8.service;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.PDPageContentStream.AppendMode;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.st.gob.pe.sifonavic8.common.FileProcessingResult;
import org.st.gob.pe.sifonavic8.common.UniqueCodeGenerator;
import org.st.gob.pe.sifonavic8.dto.CargaFechaFallecidoDTO;
import org.st.gob.pe.sifonavic8.dto.CargaHerederosPersonaDTO;
import org.st.gob.pe.sifonavic8.dto.CargaInsertHerederoDTO;
import org.st.gob.pe.sifonavic8.mapper.CargaPreviaMapper;
import org.st.gob.pe.sifonavic8.util.Constantes;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Service
public class FileUploadService {


    @Autowired
    private CargaPreviaMapper cargaPreviaMapper;

    private final CargaPreviaService cargaPreviaService;


    @Autowired
    public FileUploadService(CargaPreviaService cargaPreviaService) {
        this.cargaPreviaService = cargaPreviaService;
    }



    public FileProcessingResult processFileFechaFallecido(MultipartFile file, String usuarioActual) {

        List<CargaFechaFallecidoDTO> listaDocumentos = new ArrayList<>();
        StringBuilder errores = new StringBuilder();

        // Verificar si el usuarioActual está vacío o es null
        if (usuarioActual == null || usuarioActual.isEmpty()) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("message", "Usuario no autenticado. Por favor, inicie sesión.");
            return new FileProcessingResult(true, errorResponse,null); // Retornar un error claro
        }

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8))) {
            String line;
            boolean isFirstLine = true; // Omitir encabezados

            while ((line = reader.readLine()) != null) {
                if (isFirstLine) {
                    isFirstLine = false;
                    continue;
                }

                String[] data = line.split(";");
                if (data.length == 6) {
                    String tipDocumento = data[0];
                    String docNumber = data[1];
                    String deathDate = data[5];

                    String regex = "^([0-2][0-9]|(3)[0-1])/((0)[0-9]|(1)[0-2])/\\d{4}$";

                    tipDocumento = validarTipoDocumento(tipDocumento, line, errores);
                    validarNumeroDocumento(tipDocumento, docNumber, line, errores);
                    validarFecha(deathDate, docNumber, line, errores, regex);


                    validarExistsByNumeroDocumento(tipDocumento, docNumber, line, errores);

                    if (!errores.toString().isEmpty()) continue; // Si hay errores previos, saltar la línea

                    try {
                        String finalformattedDeathDate = Constantes.convertDateFormat(deathDate);
                        CargaFechaFallecidoDTO dto = new CargaFechaFallecidoDTO(tipDocumento, docNumber, finalformattedDeathDate, usuarioActual);
                        listaDocumentos.add(dto);
                    } catch (Exception e) {
                        errores.append("Error al formatear la fecha en la línea: ").append(line).append("\n");
                    }

                } else {
                    errores.append("Datos incompletos: ").append(line).append("\n");
                }
            }

            if (errores.length() > 0) {
                // Crear archivo de errores
//                Path tempDirectory = Files.createTempDirectory("errores");
//                String errorFileName = "errores_" + System.currentTimeMillis() + ".txt";
//                Path errorFilePath = Paths.get(errorFileName);
//
//                Files.write(errorFilePath, errores.toString().getBytes(StandardCharsets.UTF_8));
//
//                Map<String, String> errorResponse = new HashMap<>();
//                errorResponse.put("message", "Errores encontrados en el archivo. Descargue el archivo de errores.");
//                errorResponse.put("fileName", errorFileName);
//
//                return new FileProcessingResult(true, errorResponse,errorFileName);

                Map<String, String> errorResponse = new HashMap<>();
                errorResponse.put("message", "Errores encontrados en el archivo.");
                errorResponse.put("errors", errores.toString()); // Agregar detalles de los errores

                return new FileProcessingResult(true, errorResponse, null);

            }

            // Proceso en bloques de 80
            int batchSize = 20;
            int totalRecords = listaDocumentos.size();
            for(int i = 0; i < totalRecords; i+= batchSize){
                int endIndex = Math.min(i + batchSize, totalRecords);
                List<CargaFechaFallecidoDTO> subList = listaDocumentos.subList(i, endIndex);

                // Procesar el bloque actual
                cargaPreviaService.procesarFechaFallecidoCargarActualizar(listaDocumentos);
                cargaPreviaService.procesarCargaActualizaPersonaValidaMasiva(listaDocumentos);
                cargaPreviaService.procesarProcedureVerificaHeredero();
            }

            // Generar PDF de resumen
            // String resumenFileName = generatePdfSummary(listaDocumentos, usuarioActual);

            //      Map<String, String> successResponse = new HashMap<>();
            //      successResponse.put("message", "El archivo se procesó correctamente. Descargue el archivo de resumen.");
            //      successResponse.put("fileName", resumenFileName);
            //
            //      return new FileProcessingResult(false, successResponse,resumenFileName);

            // Generar respuesta de éxito sin PDF
            Map<String, String> successResponse = new HashMap<>();
            successResponse.put("message", "El archivo se procesó correctamente.");
            return new FileProcessingResult(false, successResponse, null);

        } catch (Exception e) {
            e.printStackTrace();
            Map<String, String> errorResponse = new HashMap<>();
            String errorMessage = "Error interno del servidor";
            if (e.getCause() instanceof SQLException) {
                SQLException sqlEx = (SQLException) e.getCause();
                errorMessage = "Error en la ejecución del SP: " + sqlEx.getMessage();
            }
            errorResponse.put("message", errorMessage);
            return new FileProcessingResult(true, errorResponse,null);
        }
    }

    public FileProcessingResult processFileActualizarHeredero(MultipartFile file, String usuarioActual) {

        List<CargaHerederosPersonaDTO> listaCargaHerederosPersona = new ArrayList<>();
        StringBuilder errores = new StringBuilder();
        // Verificar si el usuarioActual está vacío o es null
        if (usuarioActual == null || usuarioActual.isEmpty()) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("message", "Usuario no autenticado. Por favor, inicie sesión.");
            return new FileProcessingResult(true, errorResponse,null); // Retornar un error claro
        }

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8))) {
            String line;
            boolean isFirstLine = true;

            while ((line = reader.readLine()) != null) {
                if (isFirstLine) {
                    isFirstLine = false; // Omitir la primera línea
                    continue;
                }
                String[] data = line.split(";");
                if (data.length > 4) {
                    String tipDocumento = data[0];
                    String docuNumber = data[1];
                    String nombre = data[2];
                    String apPaterno = data[3];
                    String apMaterno = data[4];

                    tipDocumento = validarTipoDocumento(tipDocumento, line, errores);
                    validarNumeroDocumento(tipDocumento, docuNumber, line, errores);
                    validarNombre(nombre, line, errores);
                    if (!errores.toString().isEmpty()) continue; // Si hay errores previos, saltar la línea

                    CargaHerederosPersonaDTO dto = new CargaHerederosPersonaDTO(tipDocumento, docuNumber, nombre, apPaterno, apMaterno);
                    listaCargaHerederosPersona.add(dto);

                } else {
                    errores.append("Datos incompletos: ").append(line).append("\n");
                }
            }

            if (errores.length() > 0) {
                String errorFileName = "errores_" + System.currentTimeMillis() + ".txt";
                Path errorFilePath = Paths.get(errorFileName);
                Files.write(errorFilePath, errores.toString().getBytes(StandardCharsets.UTF_8));

                Map<String, String> errorResponse = new HashMap<>();
                errorResponse.put("message", "Errores encontrados en el archivo. Descargue el archivo de errores.");
                errorResponse.put("fileName", errorFileName);

                return new FileProcessingResult(true, errorResponse,errorFileName);
            }

            // Proceso en bloques de 20
            int batchSize = 20;
            int totalRecords = listaCargaHerederosPersona.size();
            for (int i = 0; i < totalRecords; i += batchSize) {
                int endIndex = Math.min(i + batchSize, totalRecords); // Determinar el índice final del bloque
                List<CargaHerederosPersonaDTO> subList = listaCargaHerederosPersona.subList(i, endIndex);

                // Procesar el bloque actual
                cargaPreviaService.procesarCargaActualizaHerederosMasiva(subList);
                cargaPreviaService.procesarActualizaHerederosReniec(usuarioActual);
                cargaPreviaService.procesarActualizaHerederosGPEC_Persona();
            }

            // Generar resumen después de procesar todos los bloques
//            String resumenFileName = generatedPdfSummaryUpdateHeredero(listaCargaHerederosPersona, usuarioActual);
//
//            Map<String, String> successResponse = new HashMap<>();
//            successResponse.put("message", "El archivo se procesó correctamente. Descargue el archivo de resumen.");
//            successResponse.put("fileName", resumenFileName);
//
//            return new FileProcessingResult(false, successResponse,resumenFileName);

            // Generar respuesta de éxito sin PDF
            Map<String, String> successResponse = new HashMap<>();
            successResponse.put("message", "El archivo se procesó correctamente.");

            return new FileProcessingResult(false, successResponse, null);

        } catch (Exception e) {
            e.printStackTrace();
            Map<String, String> errorResponse = new HashMap<>();
            String errorMessage = "Error interno al servidor";
            if (e.getCause() instanceof SQLException) {
                SQLException sqlEx = (SQLException) e.getCause();
                errorMessage = "Error en la ejecución del SP: " + sqlEx.getMessage();
            }

            errorResponse.put("message", errorMessage);
            return new FileProcessingResult(true, errorResponse,null);
        }
    }

/*    public FileProcessingResult processFileInsertHeredero(MultipartFile file, String usuarioActual) {

        List<CargaInsertHerederoDTO> listaInsertCargaHerederosPersona = new ArrayList<>();
        StringBuilder errores = new StringBuilder();

        // Verificar si el usuarioActual está vacío o es null
        if (usuarioActual == null || usuarioActual.isEmpty()) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("message", "Usuario no autenticado. Por favor, inicie sesión.");
            return new FileProcessingResult(true, errorResponse,null); // Retornar un error claro
        }
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8))) {
            String line;
            boolean isFirstLine = true;

            while ((line = reader.readLine()) != null) {
                if (isFirstLine) {
                    isFirstLine = false;
                    continue;
                }

                String[] data = line.split(";");
                if (data.length > 5) {
                    String tipDocumento = data[0];
                    String docNumber = data[1];
                    String nombre = data[2];
                    String apPaterno = data[3];
                    String apMaterno = data[4];
                    String deathDate = data[5];
                    String regex = "^([0-2][0-9]|(3)[0-1])/((0)[0-9]|(1)[0-2])/\\d{4}$";

                    tipDocumento = validarTipoDocumento(tipDocumento, line, errores);
                    validarNumeroDocumento(tipDocumento, docNumber, line, errores);
                    validarNombre(nombre, line, errores);
                    validarFecha(deathDate, docNumber, line, errores, regex);

                    if (!errores.toString().isEmpty()) continue;

                    try {
                        String formattedDeathDate = Constantes.convertDateFormat(deathDate);
                        CargaInsertHerederoDTO dto = new CargaInsertHerederoDTO(tipDocumento, docNumber, nombre, apPaterno, apMaterno, formattedDeathDate);
                        listaInsertCargaHerederosPersona.add(dto);
                    } catch (Exception e) {
                        errores.append("Error al formatear la fecha en la línea: ").append(line).append("\n");
                    }

                } else {
                    errores.append("Datos incompletos: ").append(line).append("\n");
                }
            }
            if (errores.length() > 0) {
                String errorFileName = "errores_" + System.currentTimeMillis() + ".txt";
                Path errorFilePath = Paths.get(errorFileName);
                Files.write(errorFilePath, errores.toString().getBytes(StandardCharsets.UTF_8));

                Map<String, String> errorResponse = new HashMap<>();
                errorResponse.put("message", "Errores encontrados en el archivo. Descargue el archivo de errores.");
                errorResponse.put("fileName", errorFileName);

                return new FileProcessingResult(true, errorResponse,errorFileName);
            }

            int batchSize = 20;
            int totalRecords = listaInsertCargaHerederosPersona.size();
            for(int i = 0; i< totalRecords; i+= batchSize){
                int endIndex = Math.min(i + batchSize, totalRecords);
                List<CargaInsertHerederoDTO> subList = listaInsertCargaHerederosPersona.subList(i,endIndex);

                // Procesar el bloque actual
                cargaPreviaService.procesarCargaInsertarHerederosMasiva(listaInsertCargaHerederosPersona);
                cargaPreviaService.procesarInsertarHerederos();
            }


            // Generar PDF de resumen
//            String resumenFileName = generatedPdfSummaryInsertHeredero(listaInsertCargaHerederosPersona, usuarioActual);

            Map<String, String> successResponse = new HashMap<>();
//            successResponse.put("message", "El archivo se procesó correctamente. Descargue el archivo de resumen.");
//            successResponse.put("fileName", resumenFileName);

//            return new FileProcessingResult(false, successResponse,resumenFileName);
            successResponse.put("message", "El archivo se procesó correctamente.");

            return new FileProcessingResult(false, successResponse, null);

        } catch (Exception e) {
            e.printStackTrace();
            Map<String, String> errorResponse = new HashMap<>();
            String errorMessage = "Error interno del servidor";
            if (e.getCause() instanceof SQLException) {
                SQLException sqlEx = (SQLException) e.getCause();
                errorMessage = "Error en la ejecución del SP: " + sqlEx.getMessage();
            }
            errorResponse.put("message", errorMessage);
            return new FileProcessingResult(true, errorResponse,null);
        }

    }*/

    public FileProcessingResult processFileInsertHeredero(MultipartFile file, String usuarioActual) {

        List<CargaInsertHerederoDTO> listaInsertCargaHerederosPersona = new ArrayList<>();
        StringBuilder errores = new StringBuilder();

        // Verificar si el usuarioActual está vacío o es null
        if (usuarioActual == null || usuarioActual.isEmpty()) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("message", "Usuario no autenticado. Por favor, inicie sesión.");
            return new FileProcessingResult(true, errorResponse, null); // Retornar un error claro
        }
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8))) {
            String line;
            boolean isFirstLine = true;

            while ((line = reader.readLine()) != null) {
                if (isFirstLine) {
                    isFirstLine = false;
                    continue;
                }

                String[] data = line.split(";");
                if (data.length > 5) {
                    String tipDocumento = data[0];
                    String docNumber = data[1];
                    String nombre = data[2];
                    String apPaterno = data[3];
                    String apMaterno = data[4];
                    String deathDate = data[5];
                    String regex = "^([0-2][0-9]|(3)[0-1])/((0)[0-9]|(1)[0-2])/\\d{4}$";

                    tipDocumento = validarTipoDocumento(tipDocumento, line, errores);
                    validarNumeroDocumento(tipDocumento, docNumber, line, errores);
                    validarNombre(nombre, line, errores);
                    validarFecha(deathDate, docNumber, line, errores, regex);

                    if (errores.length() > 0) continue;

                    try {
                        String formattedDeathDate = Constantes.convertDateFormat(deathDate);
                        CargaInsertHerederoDTO dto = new CargaInsertHerederoDTO(tipDocumento, docNumber, nombre, apPaterno, apMaterno, formattedDeathDate);
                        listaInsertCargaHerederosPersona.add(dto);
                    } catch (Exception e) {
                        errores.append("Error al formatear la fecha en la línea: ").append(line).append("\n");
                    }

                } else {
                    errores.append("Datos incompletos: ").append(line).append("\n");
                }
            }
            if (errores.length() > 0) {
                // En lugar de escribir en un archivo, incluir los errores directamente en la respuesta
                Map<String, String> errorResponse = new HashMap<>();
                errorResponse.put("message", "Errores encontrados en el archivo.");
                errorResponse.put("errors", errores.toString()); // Agregar detalles de los errores

                return new FileProcessingResult(true, errorResponse, null);
            }

            int batchSize = 20;
            int totalRecords = listaInsertCargaHerederosPersona.size();
            for(int i = 0; i < totalRecords; i += batchSize){
                int endIndex = Math.min(i + batchSize, totalRecords);
                List<CargaInsertHerederoDTO> subList = listaInsertCargaHerederosPersona.subList(i, endIndex);

                // Procesar el bloque actual
                cargaPreviaService.procesarCargaInsertarHerederosMasiva(subList); // Asegúrate de pasar la sublista
                cargaPreviaService.procesarInsertarHerederos();
            }

            // Generar respuesta de éxito sin PDF
            Map<String, String> successResponse = new HashMap<>();
            successResponse.put("message", "El archivo se procesó correctamente.");

            return new FileProcessingResult(false, successResponse, null);

        } catch (Exception e) {
            e.printStackTrace();
            Map<String, String> errorResponse = new HashMap<>();
            String errorMessage = "Error interno del servidor";
            if (e.getCause() instanceof SQLException) {
                SQLException sqlEx = (SQLException) e.getCause();
                errorMessage = "Error en la ejecución del SP: " + sqlEx.getMessage();
            }
            errorResponse.put("message", errorMessage);
            return new FileProcessingResult(true, errorResponse, null);
        }

    }


    // Generacion de PDFs
    private String generatePdfSummary(List<CargaFechaFallecidoDTO> listaDocumentos, String usuarioActual) throws IOException {
        UniqueCodeGenerator generator = new UniqueCodeGenerator();
        String unicoCodigo = generator.generateUniqueCode();
        String resumenFileName = unicoCodigo + ".pdf";

        try (PDDocument document = new PDDocument()) {
            int startIndex = 0;
            int pageIndex = 0;

            while (startIndex < listaDocumentos.size()) {
                addPageWithContentFechaFallecido(document, listaDocumentos, usuarioActual, startIndex, pageIndex, resumenFileName);
                startIndex += getMaxRowsPerPage(document, pageIndex);
                pageIndex++;
            }

            document.save(resumenFileName);
        }

        return resumenFileName;
    }

    private void addPageWithContentFechaFallecido(PDDocument document, List<CargaFechaFallecidoDTO> listaDocumentos, String usuarioActual, int startIndex, int pageIndex, String resumenFileName) {

        PDPage page = new PDPage(PDRectangle.A4);
        document.addPage(page);

        try (PDPageContentStream contentStream = new PDPageContentStream(document, page, AppendMode.APPEND, true, true)) {
            float margin = 30;
            float yStart = page.getMediaBox().getHeight() - margin; // Ajuste según el tamaño de la página
            float yPosition = yStart;
            float rowHeight = 20;
            float tableWidth = page.getMediaBox().getWidth() - 2 * margin;
            float headerHeight = 50; // Espacio para encabezado y detalles

            // Título y detalles
            yPosition -= 40; // Espacio desde el borde superior
            contentStream.setFont(PDType1Font.HELVETICA_BOLD, 16);
            contentStream.beginText();
            contentStream.newLineAtOffset(margin, yPosition);
            contentStream.showText("Resumen de la Carga de la actualización de fechas datos fallecidos");
            contentStream.endText();


            yPosition -= 40;
            contentStream.setFont(PDType1Font.HELVETICA, 12);
            contentStream.beginText();
            contentStream.newLineAtOffset(margin, yPosition);
//            contentStream.showText("Código generado: " + resumenFileName.replace(".pdf", ""));
            contentStream.newLineAtOffset(0, -15);
            contentStream.showText("Usuario: " + usuarioActual);
            contentStream.newLineAtOffset(0, -15);
            contentStream.showText("Fecha: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")));
            contentStream.newLineAtOffset(0, -15);
            contentStream.showText("Cantidad de registros procesados: " + listaDocumentos.size());
            contentStream.endText();
            yPosition -= 70; // Espacio antes de la tabla
            // Encabezados de la tabla
            contentStream.setFont(PDType1Font.HELVETICA_BOLD, 10);
            contentStream.beginText();
            contentStream.newLineAtOffset(margin, yPosition);
            contentStream.showText("N°");
            contentStream.newLineAtOffset(100, 0);
            contentStream.showText("Tipo Doc");
            contentStream.newLineAtOffset(100, 0);
            contentStream.showText("Número Documento");
            contentStream.newLineAtOffset(100, 0);
            contentStream.showText("Fecha de fallecimiento");
            contentStream.endText();

            yPosition -= 30; // Espacio después del encabezado

            // Datos de la tabla
            contentStream.setFont(PDType1Font.HELVETICA, 10);
            int maxRowsPerPage = getMaxRowsPerPage(document, pageIndex);
            int endIndex = Math.min(startIndex + maxRowsPerPage, listaDocumentos.size());
            for (int i = startIndex; i < endIndex; i++) {
                CargaFechaFallecidoDTO dto = listaDocumentos.get(i);
                yPosition -= rowHeight;

                if (yPosition <= margin) {
                    contentStream.close();
                    return; // No hay suficiente espacio para más filas en esta página
                }

                contentStream.beginText();
                contentStream.newLineAtOffset(margin, yPosition);
                contentStream.showText(String.valueOf(i + 1));
                contentStream.newLineAtOffset(100, 0);
                contentStream.showText(truncateText(dto.getTipDocumento(), 30));
                contentStream.newLineAtOffset(100, 0);
                contentStream.showText(truncateText(dto.getDocTitular(), 30));
                contentStream.newLineAtOffset(100, 0);
                contentStream.showText(truncateText(dto.getFormattedFechaFallecido(), 30));
                contentStream.endText();

                addFooter(document, page, pageIndex + 1);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private String generatedPdfSummaryUpdateHeredero(List<CargaHerederosPersonaDTO> listaCargaHerederosPersona, String usuarioActual) throws IOException {
        UniqueCodeGenerator generator = new UniqueCodeGenerator();
        String unicoCodigo = generator.generateUniqueCode();
        String resumenFileName = unicoCodigo + ".pdf";

        try (PDDocument document = new PDDocument()) {
            int startIndex = 0;
            int pageIndex = 0;

            while (startIndex < listaCargaHerederosPersona.size()) {
                addPageWithContentUpdateHeredero(document, listaCargaHerederosPersona, usuarioActual, startIndex, pageIndex, resumenFileName);
                startIndex += getMaxRowsPerPage(document, pageIndex);
                pageIndex++;
            }

            document.save(resumenFileName);
        }

        return resumenFileName;
    }

    private void addPageWithContentUpdateHeredero(PDDocument document, List<CargaHerederosPersonaDTO> listaCargaHerederosPersona, String usuarioActual, int startIndex, int pageIndex, String resumenFileName) throws IOException {
        PDPage page = new PDPage(PDRectangle.A3);
        document.addPage(page);

        try (PDPageContentStream contentStream = new PDPageContentStream(document, page, AppendMode.APPEND, true, true)) {
            float margin = 30;
            float yStart = page.getMediaBox().getHeight() - margin; // Ajuste según el tamaño de la página
            float yPosition = yStart;
            float rowHeight = 20;
            float tableWidth = page.getMediaBox().getWidth() - 2 * margin;
            float headerHeight = 50; // Espacio para encabezado y detalles

            // Título y detalles
            yPosition -= 40; // Espacio desde el borde superior
            contentStream.setFont(PDType1Font.HELVETICA_BOLD, 16);
            contentStream.beginText();
            contentStream.newLineAtOffset(margin, yPosition);
            contentStream.showText("Resumen de la Carga de la actualización de datos Herederos");
            contentStream.endText();

            yPosition -= 40;
            contentStream.setFont(PDType1Font.HELVETICA, 12);
            contentStream.beginText();
            contentStream.newLineAtOffset(margin, yPosition);
//            contentStream.showText("Código generado: " + resumenFileName.replace(".pdf", ""));
            contentStream.newLineAtOffset(0, -15);
            contentStream.showText("Usuario: " + usuarioActual);
            contentStream.newLineAtOffset(0, -15);
            contentStream.showText("Fecha: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")));
            contentStream.newLineAtOffset(0, -15);
            contentStream.showText("Cantidad de registros procesados: " + listaCargaHerederosPersona.size());
            contentStream.endText();
            yPosition -= 70; // Espacio antes de la tabla
            // Encabezados de la tabla
            contentStream.setFont(PDType1Font.HELVETICA_BOLD, 10);
            contentStream.beginText();
            contentStream.newLineAtOffset(margin, yPosition);
            contentStream.showText("N°");
            contentStream.newLineAtOffset(100, 0);
            contentStream.showText("Tipo Doc");
            contentStream.newLineAtOffset(100, 0);
            contentStream.showText("Número Documento");
            contentStream.newLineAtOffset(100, 0);
            contentStream.showText("Nombres");
            contentStream.newLineAtOffset(150, 0);
            contentStream.showText("Apellido Paterno");
            contentStream.newLineAtOffset(100, 0);
            contentStream.showText("Apellido Materno");
            contentStream.endText();

            yPosition -= 30; // Espacio después del encabezado

            // Datos de la tabla
            contentStream.setFont(PDType1Font.HELVETICA, 10);
            int maxRowsPerPage = getMaxRowsPerPage(document, pageIndex);
            int endIndex = Math.min(startIndex + maxRowsPerPage, listaCargaHerederosPersona.size());
            for (int i = startIndex; i < endIndex; i++) {
                CargaHerederosPersonaDTO dto = listaCargaHerederosPersona.get(i);
                yPosition -= rowHeight;

                if (yPosition <= margin) {
                    contentStream.close();
                    return; // No hay suficiente espacio para más filas en esta página
                }

                contentStream.beginText();
                contentStream.newLineAtOffset(margin, yPosition);
                contentStream.showText(String.valueOf(i + 1));
                contentStream.newLineAtOffset(100, 0);
                contentStream.showText(truncateText(dto.getTipDocumento(), 30));
                contentStream.newLineAtOffset(100, 0);
                contentStream.showText(truncateText(dto.getDocHeredero(), 30));
                contentStream.newLineAtOffset(100, 0);
                contentStream.showText(truncateText(dto.getNombre(), 30));
                contentStream.newLineAtOffset(150, 0);
                contentStream.showText(truncateText(dto.getApPaterno(), 30));
                contentStream.newLineAtOffset(100, 0);
                contentStream.showText(truncateText(dto.getApMaterno(), 30));
                contentStream.endText();
            }
        }
        // Añadir pie de página con número de página
        addFooter(document, page, pageIndex + 1);
    }


    private String generatedPdfSummaryInsertHeredero(List<CargaInsertHerederoDTO> listaInsertCargaHerederosPersona, String usuarioActual) throws IOException {
        UniqueCodeGenerator generator = new UniqueCodeGenerator();
        String unicoCodigo = generator.generateUniqueCode();
        String resumenFileName = unicoCodigo + ".pdf";

        try (PDDocument document = new PDDocument()) {
            int startIndex = 0;
            int pageIndex = 0;

            while (startIndex < listaInsertCargaHerederosPersona.size()) {
                addPageWithContentInsertHeredero(document, listaInsertCargaHerederosPersona, usuarioActual, startIndex, pageIndex, resumenFileName);
                startIndex += getMaxRowsPerPage(document, pageIndex);
                pageIndex++;
            }

            document.save(resumenFileName);
        }

        return resumenFileName;
    }

    private void addPageWithContentInsertHeredero(PDDocument document, List<CargaInsertHerederoDTO> listaInsertCargaHerederosPersona, String usuarioActual, int startIndex, int pageIndex, String resumenFileName) throws IOException {
        PDPage page = new PDPage(PDRectangle.A3);
        document.addPage(page);

        try (PDPageContentStream contentStream = new PDPageContentStream(document, page, AppendMode.APPEND, true, true)) {
            float margin = 30;
            float yStart = page.getMediaBox().getHeight() - margin; // Ajuste según el tamaño de la página
            float yPosition = yStart;
            float rowHeight = 20;
            float tableWidth = page.getMediaBox().getWidth() - 2 * margin;
            float headerHeight = 50; // Espacio para encabezado y detalles

            // Título y detalles
            yPosition -= 40; // Espacio desde el borde superior
            contentStream.setFont(PDType1Font.HELVETICA_BOLD, 16);
            contentStream.beginText();
            contentStream.newLineAtOffset(margin, yPosition);
            contentStream.showText("Resumen de la Carga de la inserción de datos Herederos");
            contentStream.endText();

            yPosition -= 40;
            contentStream.setFont(PDType1Font.HELVETICA, 12);
            contentStream.beginText();
            contentStream.newLineAtOffset(margin, yPosition);
//            contentStream.showText("Código generado: " + resumenFileName.replace(".pdf", ""));
            contentStream.newLineAtOffset(0, -15);
            contentStream.showText("Usuario: " + usuarioActual);
            contentStream.newLineAtOffset(0, -15);
            contentStream.showText("Fecha: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")));
            contentStream.newLineAtOffset(0, -15);
            contentStream.showText("Cantidad de registros procesados: " + listaInsertCargaHerederosPersona.size());
            contentStream.endText();
            yPosition -= 70; // Espacio antes de la tabla
            // Encabezados de la tabla
            contentStream.setFont(PDType1Font.HELVETICA_BOLD, 10);
            contentStream.beginText();
            contentStream.newLineAtOffset(margin, yPosition);
            contentStream.showText("N°");
            contentStream.newLineAtOffset(50, 0);
            contentStream.showText("Tipo Doc");
            contentStream.newLineAtOffset(50, 0);
            contentStream.showText("Número Doc");
            contentStream.newLineAtOffset(80, 0);
            contentStream.showText("Nombres");
            contentStream.newLineAtOffset(150, 0);
            contentStream.showText("Apellido Paterno");
            contentStream.newLineAtOffset(150, 0);
            contentStream.showText("Apellido Materno");
            contentStream.newLineAtOffset(200, 0);
            contentStream.showText("Fecha Nacimiento");
            contentStream.endText();

            yPosition -= 30; // Espacio después del encabezado

            // Datos de la tabla
            contentStream.setFont(PDType1Font.HELVETICA, 10);
            int maxRowsPerPage = getMaxRowsPerPage(document, pageIndex);
            int endIndex = Math.min(startIndex + maxRowsPerPage, listaInsertCargaHerederosPersona.size());
            for (int i = startIndex; i < endIndex; i++) {
                CargaInsertHerederoDTO dto = listaInsertCargaHerederosPersona.get(i);
                yPosition -= rowHeight;

                if (yPosition <= margin) {
                    contentStream.close();
                    return; // No hay suficiente espacio para más filas en esta página
                }

                contentStream.beginText();
                contentStream.newLineAtOffset(margin, yPosition);
                contentStream.showText(String.valueOf(i + 1));
                contentStream.newLineAtOffset(50, 0);
                contentStream.showText(truncateText(dto.getTipDocumento(), 30));
                contentStream.newLineAtOffset(50, 0);
                contentStream.showText(truncateText(dto.getDocHeredero(), 30));
                contentStream.newLineAtOffset(80, 0);
                contentStream.showText(truncateText(dto.getNombre(), 30));
                contentStream.newLineAtOffset(150, 0);
                contentStream.showText(truncateText(dto.getApPaterno(), 30));
                contentStream.newLineAtOffset(150, 0);
                contentStream.showText(truncateText(dto.getApMaterno(), 30));
                contentStream.newLineAtOffset(200, 0);
                contentStream.showText(truncateText(dto.getFechaNacimiento(), 30));
                contentStream.endText();
            }
        }
        // Añadir pie de página con número de página
        addFooter(document, page, pageIndex + 1);
    }

    private String truncateText(String text, int maxWidth) {
        // Método para truncar texto si es más largo que el ancho permitido
        if (text.length() > maxWidth) {
            return text.substring(0, maxWidth) + "...";
        } else {
            return text;
        }
    }

    private int getMaxRowsPerPage(PDDocument document, int pageIndex) {
        return 45;
    }

    private void addFooter(PDDocument document, PDPage page, int pageIndex) throws IOException {
        try (PDPageContentStream contentStream = new PDPageContentStream(document, page, AppendMode.APPEND, true, true)) {
            contentStream.setFont(PDType1Font.HELVETICA, 10);
            contentStream.beginText();

            float x = page.getMediaBox().getWidth() - 50; // Ajustar posición en X si es necesario
            float y = 30; // Posición cerca del borde inferior

            contentStream.newLineAtOffset(x, y);
            contentStream.showText("Página " + pageIndex);
            contentStream.endText();
        }
    }


    //  Validaciones
    private String validarTipoDocumento(String tipoDocumento, String line, StringBuilder errores) {

        // Verificar si el tipo de documento está vacío
        if (tipoDocumento.isEmpty()) {
            errores.append("Error en línea: '").append(line).append("': El tipo de documento no debe estar en blanco.\n");
            return tipoDocumento; // Termina aquí si está vacío, para no intentar convertirlo
        }

        // Intentar convertir el tipo de documento a número
        try {
            int tipoDocNumber = Integer.parseInt(tipoDocumento);
            // Si es un número entre 1 y 9, lo formateamos a dos dígitos
            if (tipoDocNumber >= 1 && tipoDocNumber <= 9) {
                tipoDocumento = String.format("%02d", tipoDocNumber);
            }
        } catch (NumberFormatException e) {
            // Si no es un número válido, agregar un mensaje de error
            errores.append("Error en línea: '").append(line).append("': El tipo de documento no es un número válido: '").append(tipoDocumento).append("'.\n");
            return tipoDocumento; // Termina aquí si no es un número válido
        }
        return tipoDocumento;
    }

    private void validarNumeroDocumento(String tipDocumento, String docNumber, String line, StringBuilder errores) {
        if (docNumber.isEmpty()) {
            errores.append("Error en línea: '").append(line).append("': El número de documento no debe estar en blanco.\n");
        } else if (docNumber.length() > 10) {
            errores.append("Error en línea: '").append(line).append("': El número de documento es demasiado largo. \n");
        }
    }

    private void validarNombre(String nombre, String line, StringBuilder errores) {
        if (nombre.isEmpty()) {
            errores.append("Error en línea: '").append(line).append("': El nombre no puede estar en blanco.\n");
        }
    }

    private void validarFecha(String deathDate, String docNumber, String line, StringBuilder errores, String regex) {
        // Validar que la fecha no esté vacía
        if (deathDate.isEmpty()) {
            errores.append("Error en línea: '").append(line).append("': Fecha vacía\n");
            return;
        }

        //Separar el día, mes y año
        String[] dateParts = deathDate.split("/");
        if (dateParts.length == 3) {
            try {
                //Formatear día y mes
                String day = String.format("%02d", Integer.parseInt(dateParts[0]));
                String month = String.format("%02d", Integer.parseInt(dateParts[1]));
                String year = dateParts[2];

                deathDate = day + "/" + month + "/" + year;
            } catch (NumberFormatException e) {
                errores.append("Error en línea'").append(line).append("': Fecha inválida").append(deathDate).append("\n");
                return;
            }
        } else {
            errores.append("Error en línea: '").append(line).append("': Fecha inválida ").append(deathDate).append("\n");
            return;
        }

        // Validar el formato de la fecha con la expresión regular
        if (!deathDate.matches(regex)) {
            errores.append("Error en línea: '").append(line).append("': Formato de fecha incorrecto: ").append(deathDate).append("\n");
        }

    }

    private void validarExistsByNumeroDocumento(String tipoDocumento, String docNumber, String line, StringBuilder errores) {

        if (!cargaPreviaMapper.existsByNumeroDocumento(tipoDocumento, docNumber)) {
            errores.append("Error en línea: '").append(line).append("': No existe registro en la base de datos.\n");
        }

    }


}
