package org.example.personmanagerapi.person.search.model;


import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Retiree extends Person {
    private double pension;
    private int yearsWorked;

    // Getters and setters
}

