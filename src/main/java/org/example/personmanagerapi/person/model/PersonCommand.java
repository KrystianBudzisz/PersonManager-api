package org.example.personmanagerapi.person.model;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PersonCommand {
    @NotBlank(message = "Type is mandatory")
    private String type;

    @NotBlank(message = "First name is mandatory")
    private String firstName;

    @NotBlank(message = "Last name is mandatory")
    private String lastName;

    @NotBlank(message = "PESEL is mandatory")
    @Size(min = 11, max = 11, message = "PESEL must be 11 characters long")
    private String pesel;

    @Positive(message = "Height must be positive")
    @NotNull(message = "Height is mandatory")
    private Double height;

    @Positive(message = "Weight must be positive")
    @NotNull(message = "Weight is mandatory")
    private Double weight;

    @Email(message = "Email should be valid")
    @NotBlank(message = "Email is mandatory")
    private String email;

    @NotNull(message = "Type-specific fields cannot be null")
    private Map<String, Object> typeSpecificFields = new HashMap<>();
}
