package org.example.personmanagerapi.exception;

public class PositionOverlapException extends RuntimeException {
    public PositionOverlapException(String message) {
        super(message);
    }
}
