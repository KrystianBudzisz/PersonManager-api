package org.example.personmanagerapi.rentiree.model;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.personmanagerapi.person.model.PersonCommand;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RetireeCommand extends PersonCommand {

    @Positive(message = "Pension must be positive")
    @NotNull(message = "Pension is mandatory")
    private Double pension;

    @Positive(message = "Years worked must be positive")
    @NotNull(message = "Years worked is mandatory")
    private Integer yearsWorked;
}
