package org.example.personmanagerapi.position;



import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class PositionDTO {
    private String positionName;
    private double salary;
    private LocalDate startDate;
    private LocalDate endDate;

}
