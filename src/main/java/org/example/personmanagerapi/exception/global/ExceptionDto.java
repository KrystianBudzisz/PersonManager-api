package org.example.personmanagerapi.exception.global;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

@RequiredArgsConstructor
@Getter
public class ExceptionDto {

    private final LocalDateTime timestamp = LocalDateTime.now();
    private final String message;

}