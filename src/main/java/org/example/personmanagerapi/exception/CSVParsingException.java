package org.example.personmanagerapi.exception;

public class CSVParsingException extends RuntimeException {
    public CSVParsingException(String message, Throwable cause) {
        super(message, cause);
    }
}
