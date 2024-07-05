package org.example.personmanagerapi.rentiree.model;


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
public class Retiree extends Person {
    private double pension;
    private int yearsWorked;


}

