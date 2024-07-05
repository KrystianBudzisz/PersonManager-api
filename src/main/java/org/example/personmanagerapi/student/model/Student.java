package org.example.personmanagerapi.student.model;


import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.personmanagerapi.person.model.Person;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Student extends Person {
    private String universityName;
    private int yearOfStudy;
    private String fieldOfStudy;
    private double scholarship;

    public Student(UUID id, String firstName, String lastName, String pesel, double height, double weight, String email, Long version, String universityName, int yearOfStudy, String fieldOfStudy, double scholarship) {
        super(id, firstName, lastName, pesel, height, weight, email, version);
        this.universityName = universityName;
        this.yearOfStudy = yearOfStudy;
        this.fieldOfStudy = fieldOfStudy;
        this.scholarship = scholarship;
    }

}
