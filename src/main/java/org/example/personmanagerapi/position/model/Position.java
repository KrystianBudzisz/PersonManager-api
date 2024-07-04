package org.example.personmanagerapi.position;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.example.personmanagerapi.employee.Employee;

import java.time.LocalDate;
import java.util.UUID;
@Getter
@Setter
@Entity
public class Position {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "employee_id", nullable = false)
    private Employee employee;

    private String positionName;
    private double salary;
    private LocalDate startDate;
    private LocalDate endDate;

    // Getters and setters
}
