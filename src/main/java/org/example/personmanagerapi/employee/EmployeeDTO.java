package org.example.personmanagerapi.employee;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.example.personmanagerapi.person.model.PersonDTO;
import org.example.personmanagerapi.position.model.PositionDTO;

import java.util.Set;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeDTO extends PersonDTO {
    private String employmentDate;
    private String currentPosition;
    private double currentSalary;
    private int numberOfPositions;
    private Set<PositionDTO> positions;

}
