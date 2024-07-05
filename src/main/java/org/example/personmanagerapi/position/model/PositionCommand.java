package org.example.personmanagerapi.position.model;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PositionCommand {

    @NotBlank(message = "Position name is mandatory")
    private String positionName;

    @Positive(message = "Salary must be positive")
    @NotNull(message = "Salary is mandatory")
    private Double salary;

    @NotNull(message = "Start date is mandatory")
    private LocalDate startDate;

    @NotNull(message = "End date is mandatory")
    @FutureOrPresent(message = "End date cannot be in the past")
    private LocalDate endDate;
}

