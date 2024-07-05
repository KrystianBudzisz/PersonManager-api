package org.example.personmanagerapi.rentiree;

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
public class RetireeDTO extends PersonDTO {
    private double pension;
    private int yearsWorked;

}

