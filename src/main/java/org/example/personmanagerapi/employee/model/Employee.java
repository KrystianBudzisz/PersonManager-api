package org.example.personmanagerapi.person.search.model;


import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.personmanagerapi.person.search.model.Person;
import org.example.personmanagerapi.position.model.Position;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class Employee extends Person {
    private String employmentDate;
    private String currentPosition;
    private double currentSalary;

    @OneToMany(mappedBy = "employee", cascade = CascadeType.ALL)
    private Set<Position> positions = new HashSet<>();

    public void addPosition(Position position) {
        positions.add(position);
        position.setEmployee(this);
    }
    // Getters and setters
}

