package org.example.personmanagerapi.person.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public abstract class PersonDTO {
    private Long id;
    private String firstName;
    private String lastName;
    private String pesel;
    private double height;
    private double weight;
    private String email;
}
