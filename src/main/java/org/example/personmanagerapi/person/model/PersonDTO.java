package org.example.personmanagerapi.person;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@SuperBuilder
public abstract class PersonDTO {
    private String firstName;
    private String lastName;
    private String pesel;
    private int height;
    private int weight;
    private String email;

    // Getters and setters
}

