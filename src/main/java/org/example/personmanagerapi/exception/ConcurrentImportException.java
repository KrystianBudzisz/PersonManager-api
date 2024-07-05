package org.example.personmanagerapi.exception;

public class ConcurrentImportException extends RuntimeException {
    public ConcurrentImportException(String message) {
        super(message);
    }

}
