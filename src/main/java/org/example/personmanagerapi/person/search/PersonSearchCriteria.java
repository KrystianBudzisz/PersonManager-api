package org.example.personmanagerapi.person.search;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PersonSearchCriteria {
    private String type;
    private String firstName;
    private String lastName;
    private LocalDate birthDateFrom;
    private LocalDate birthDateTo;
    private String pesel;
    private Double heightFrom;
    private Double heightTo;
    private Double weightFrom;
    private Double weightTo;
    private String email;

    private Double currentSalaryFrom;
    private Double currentSalaryTo;

    private Map<String, Object> dynamicCriteria = new HashMap<>();

}




