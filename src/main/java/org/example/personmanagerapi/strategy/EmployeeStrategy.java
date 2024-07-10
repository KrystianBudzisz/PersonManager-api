package org.example.personmanagerapi.strategy;

import org.example.personmanagerapi.employee.EmployeeCommand;
import org.example.personmanagerapi.employee.model.Employee;
import org.example.personmanagerapi.person.model.Person;
import org.example.personmanagerapi.person.model.PersonCommand;
import org.example.personmanagerapi.utils.CommandUtils;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class EmployeeStrategy implements PersonTypeStrategy {

    @Override
    public String getType() {
        return "employee";
    }

    @Override
    public Class<? extends Person> getPersonClass() {
        return Employee.class;
    }

    @Override
    public void insertSpecificFields(Map<String, Object> specificFields, JdbcTemplate jdbcTemplate, String pesel) {
        String employeeSql = "INSERT INTO employee (id, employment_date, current_position, current_salary) " +
                "SELECT id, ?, ?, ? FROM person WHERE pesel = ?";
        jdbcTemplate.update(employeeSql, specificFields.get("field1"), specificFields.get("field2"), Double.parseDouble(specificFields.get("field3").toString()), pesel);
    }


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

}




