package org.example.personmanagerapi.strategy;

import org.example.personmanagerapi.person.model.Person;
import org.example.personmanagerapi.person.model.PersonCommand;
import org.example.personmanagerapi.student.model.Student;
import org.example.personmanagerapi.student.model.StudentCommand;
import org.example.personmanagerapi.utils.CommandUtils;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class StudentStrategy implements PersonTypeStrategy {

    @Override
    public String getType() {
        return "student";
    }

    @Override
    public Class<? extends Person> getPersonClass() {
        return Student.class;
    }

    @Override
    public void insertSpecificFields(Map<String, Object> specificFields, JdbcTemplate jdbcTemplate, String pesel) {
        String studentSql = "INSERT INTO student (id, university_name, year_of_study, field_of_study, scholarship) " +
                "SELECT id, ?, ?, ?, ? FROM person WHERE pesel = ?";
        jdbcTemplate.update(studentSql, specificFields.get("field1"), Integer.parseInt(specificFields.get("field2").toString()), specificFields.get("field3"), Double.parseDouble(specificFields.get("field4").toString()), pesel);
    }


    @Override
    public Student preparePerson(PersonCommand personCommand) {
        StudentCommand studentCommand = CommandUtils.convertToSpecificCommand(personCommand.getTypeSpecificFields(), StudentCommand.class);

        Student student = new Student();
        student.setFirstName(personCommand.getFirstName());
        student.setLastName(personCommand.getLastName());
        student.setPesel(personCommand.getPesel());
        student.setHeight(personCommand.getHeight());
        student.setWeight(personCommand.getWeight());
        student.setEmail(personCommand.getEmail());
        student.setUniversityName(studentCommand.getUniversityName());
        student.setYearOfStudy(studentCommand.getYearOfStudy());
        student.setFieldOfStudy(studentCommand.getFieldOfStudy());
        student.setScholarship(studentCommand.getScholarship());
        return student;
    }

}



