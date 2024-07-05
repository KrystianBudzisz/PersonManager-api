package org.example.personmanagerapi.student;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.example.personmanagerapi.person.model.PersonDTO;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class StudentDTO extends PersonDTO {
    private String universityName;
    private int yearOfStudy;
    private String fieldOfStudy;
    private double scholarship;


}

