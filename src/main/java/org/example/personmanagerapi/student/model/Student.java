package org.example.personmanagerapi.student.model;


import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.personmanagerapi.person.model.Person;

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


}
