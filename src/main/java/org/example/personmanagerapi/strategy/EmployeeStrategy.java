package org.example.personmanagerapi.strategy;

import org.example.personmanagerapi.employee.EmployeeCommand;
import org.example.personmanagerapi.employee.model.Employee;
import org.example.personmanagerapi.person.model.Person;
import org.example.personmanagerapi.person.model.PersonCommand;
import org.example.personmanagerapi.utils.CommandUtils;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class EmployeeStrategy implements PersonTypeStrategy {

    @Override
    public Employee preparePerson(PersonCommand personCommand) {
        EmployeeCommand employeeCommand = CommandUtils.convertToSpecificCommand(personCommand.getTypeSpecificFields(), EmployeeCommand.class);

        Employee employee = new Employee();
        employee.setFirstName(personCommand.getFirstName());
        employee.setLastName(personCommand.getLastName());
        employee.setPesel(personCommand.getPesel());
        employee.setHeight(personCommand.getHeight());
        employee.setWeight(personCommand.getWeight());
        employee.setEmail(personCommand.getEmail());
        employee.setEmploymentDate(employeeCommand.getEmploymentDate());
        employee.setCurrentPosition(employeeCommand.getCurrentPosition());
        employee.setCurrentSalary(employeeCommand.getCurrentSalary());
        return employee;
    }

    @Override
    public List<String> getRequiredFields() {
        return Arrays.asList("employmentDate", "currentPosition", "currentSalary");
    }

    @Override
    public Map<String, Object> getDynamicCriteria() {
        Map<String, Object> criteria = new HashMap<>();
        criteria.put("employmentDateFrom", LocalDate.class);
        criteria.put("employmentDateTo", LocalDate.class);
        criteria.put("currentPosition", String.class);
        criteria.put("currentSalaryFrom", BigDecimal.class);
        criteria.put("currentSalaryTo", BigDecimal.class);
        return criteria;
    }

    @Override
    public Class<? extends Person> getPersonType() {
        return Employee.class;
    }
}



