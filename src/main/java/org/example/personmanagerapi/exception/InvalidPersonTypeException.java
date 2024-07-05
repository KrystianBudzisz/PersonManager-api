package org.example.personmanagerapi.exception;

public class InvalidPersonTypeException extends RuntimeException {
    public InvalidPersonTypeException(String message) {
        super(message);
    }
}
