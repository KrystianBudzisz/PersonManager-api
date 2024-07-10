package org.example.personmanagerapi.person.mapper;

import org.example.personmanagerapi.employee.EmployeeDTO;
import org.example.personmanagerapi.employee.model.Employee;
import org.example.personmanagerapi.person.model.Person;
import org.example.personmanagerapi.person.model.PersonDTO;
import org.example.personmanagerapi.rentiree.RetireeDTO;
import org.example.personmanagerapi.rentiree.model.Retiree;
import org.example.personmanagerapi.student.StudentDTO;
import org.example.personmanagerapi.student.model.Student;
import org.springframework.stereotype.Component;


@Component
public class PersonMapper {

    public PersonDTO toDTO(Person person) {
        if (person instanceof Student) {
            Student student = (Student) person;
            return StudentDTO.builder()
                    .id(student.getId())
                    .firstName(student.getFirstName())
                    .lastName(student.getLastName())
                    .pesel(student.getPesel())
                    .height(student.getHeight())
                    .weight(student.getWeight())
                    .email(student.getEmail())
                    .universityName(student.getUniversityName())
                    .yearOfStudy(student.getYearOfStudy())
                    .fieldOfStudy(student.getFieldOfStudy())
                    .scholarship(student.getScholarship())
                    .build();
        } else if (person instanceof Employee) {
            Employee employee = (Employee) person;
            return EmployeeDTO.builder()
                    .id(employee.getId())
                    .firstName(employee.getFirstName())
                    .lastName(employee.getLastName())
                    .pesel(employee.getPesel())
                    .height(employee.getHeight())
                    .weight(employee.getWeight())
                    .email(employee.getEmail())
                    .employmentDate(employee.getEmploymentDate())
                    .currentPosition(employee.getCurrentPosition())
                    .currentSalary(employee.getCurrentSalary())
                    .build();
        } else if (person instanceof Retiree) {
            Retiree retiree = (Retiree) person;
            return RetireeDTO.builder()
                    .id(retiree.getId())
                    .firstName(retiree.getFirstName())
                    .lastName(retiree.getLastName())
                    .pesel(retiree.getPesel())
                    .height(retiree.getHeight())
                    .weight(retiree.getWeight())
                    .email(retiree.getEmail())
                    .pension(retiree.getPension())
                    .yearsWorked(retiree.getYearsWorked())
                    .build();
        }
        return null;
    }
}

