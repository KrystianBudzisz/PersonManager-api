package org.example.personmanagerapi.exception.global;

import jakarta.validation.ConstraintViolationException;
import org.example.personmanagerapi.exception.*;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {


    @ExceptionHandler(PersonNotFoundException.class)
    public ResponseEntity<ExceptionDto> handlePersonNotFoundException(PersonNotFoundException ex) {
        ExceptionDto response = new ExceptionDto(ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    @ExceptionHandler(InvalidPersonTypeException.class)
    public ResponseEntity<ExceptionDto> handleInvalidPersonTypeException(InvalidPersonTypeException ex) {
        ExceptionDto response = new ExceptionDto(ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(PositionOverlapException.class)
    public ResponseEntity<ExceptionDto> handlePositionOverlapException(PositionOverlapException ex) {
        ExceptionDto response = new ExceptionDto(ex.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
    }

    @ExceptionHandler(ImportStatusNotFoundException.class)
    public ResponseEntity<ExceptionDto> handleImportStatusNotFoundException(ImportStatusNotFoundException ex) {
        ExceptionDto response = new ExceptionDto(ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    @ExceptionHandler(CSVProcessingException.class)
    public ResponseEntity<ExceptionDto> handleCSVProcessingException(CSVProcessingException ex) {
        ExceptionDto response = new ExceptionDto(ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(ConcurrentImportException.class)
    public ResponseEntity<ExceptionDto> handleConcurrentImportException(ConcurrentImportException ex) {
        ExceptionDto response = new ExceptionDto(ex.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
    }

    @ExceptionHandler(PositionNotFoundException.class)
    public ResponseEntity<ExceptionDto> handlePositionNotFoundException(PositionNotFoundException ex) {
        ExceptionDto response = new ExceptionDto(ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ExceptionDto> handleConstraintViolationException(ConstraintViolationException ex) {
        ExceptionDto response = new ExceptionDto("Constraint violation: " + ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(DuplicatePersonException.class)
    public ResponseEntity<ExceptionDto> handleDuplicatePersonException(DuplicatePersonException ex) {
        ExceptionDto response = new ExceptionDto(ex.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ExceptionDto> handleDataIntegrityViolationException(DataIntegrityViolationException ex) {
        ExceptionDto response = new ExceptionDto("Data integrity violation: " + ex.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ExceptionDto> handleAllExceptions(Exception ex) {
        ExceptionDto response = new ExceptionDto("An unexpected error occurred");
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
}


