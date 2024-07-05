package org.example.personmanagerapi.strategy;

import org.example.personmanagerapi.person.model.Person;
import org.example.personmanagerapi.person.model.PersonCommand;
import org.example.personmanagerapi.student.model.Student;
import org.example.personmanagerapi.student.model.StudentCommand;
import org.example.personmanagerapi.utils.CommandUtils;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class StudentStrategy implements PersonTypeStrategy {

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

    @Override
    public List<String> getRequiredFields() {
        return Arrays.asList("universityName", "yearOfStudy", "fieldOfStudy", "scholarship");
    }

    @Override
    public Map<String, Object> getDynamicCriteria() {
        Map<String, Object> criteria = new HashMap<>();
        criteria.put("universityName", String.class);
        criteria.put("yearOfStudy", Integer.class);
        criteria.put("fieldOfStudy", String.class);
        criteria.put("scholarshipFrom", BigDecimal.class);
        criteria.put("scholarshipTo", BigDecimal.class);
        return criteria;
    }

    @Override
    public Class<? extends Person> getPersonType() {
        return Student.class;
    }
}


