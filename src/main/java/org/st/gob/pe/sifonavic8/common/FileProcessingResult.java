package org.st.gob.pe.sifonavic8.common;

import java.util.Map;

public class FileProcessingResult {

    private boolean hasErrors;
    private Map<String, String> response;
    private String processedFileName; // Nuevo atributo para el nombre del archivo procesado

    public FileProcessingResult(boolean hasErrors, Map<String, String> response, String processedFileName) {
        this.hasErrors = hasErrors;
        this.response = response;
        this.processedFileName = processedFileName; // Inicialización del nuevo atributo
    }

    public boolean hasErrors() {
        return hasErrors;
    }

    public Map<String, String> getResponse() {
        return response;
    }

    public Map<String, String> getErrorResponse() {
        if (!hasErrors) {
            throw new IllegalStateException("No hay errores en el procesamiento.");
        }
        return response;
    }

    public Map<String, String> getSuccessResponse() {
        if (hasErrors) {
            throw new IllegalStateException("Hay errores en el procesamiento.");
        }
        return response;
    }

    // Nuevo método para obtener el nombre del archivo procesado
    public String getProcessedFileName() {
        if (hasErrors) {
            throw new IllegalStateException("Hay errores en el procesamiento. No se generó ningún archivo.");
        }
        return processedFileName;
    }
}
