package org.example.personmanagerapi.student.model;

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
public class StudentCommand extends PersonCommand {
    @NotBlank(message = "University name is mandatory")
    private String universityName;

    @Positive(message = "Year of study must be positive")
    @NotNull(message = "Year of study is mandatory")
    private Integer yearOfStudy;

    @NotBlank(message = "Field of study is mandatory")
    private String fieldOfStudy;

    @Positive(message = "Scholarship must be positive")
    @NotNull(message = "Scholarship is mandatory")
    private Double scholarship;


}
