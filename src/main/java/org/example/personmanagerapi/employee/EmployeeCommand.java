package org.example.personmanagerapi.employee;

import jakarta.validation.constraints.NotBlank;
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
public class EmployeeCommand extends PersonCommand {
    @NotBlank(message = "Employment date is mandatory")
    private String employmentDate;

    @NotBlank(message = "Current position is mandatory")
    private String currentPosition;

    @Positive(message = "Current salary must be positive")
    @NotNull(message = "Current salary is mandatory")
    private Double currentSalary;
}
