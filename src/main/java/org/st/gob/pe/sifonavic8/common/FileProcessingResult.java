package org.st.gob.pe.sifonavic8.common;

import java.util.Map;

public class FileProcessingResult {

    private boolean hasErrors;
    private Map<String, String> response;

    public FileProcessingResult(boolean hasErrors, Map<String, String> response) {
        this.hasErrors = hasErrors;
        this.response = response;
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
}
