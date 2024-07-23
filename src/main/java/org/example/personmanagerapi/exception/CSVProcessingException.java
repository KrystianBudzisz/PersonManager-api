package org.example.personmanagerapi.exception;

public class CSVProcessingException extends RuntimeException {
    public CSVProcessingException(String message, Throwable cause) {
        super(message, cause);
    }
}

