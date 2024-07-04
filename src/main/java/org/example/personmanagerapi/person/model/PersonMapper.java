package org.example.personmanagerapi.person;

import org.example.personmanagerapi.employee.Employee;
import org.example.personmanagerapi.employee.EmployeeDTO;
import org.example.personmanagerapi.person.model.Person;
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
                    .firstName(employee.getFirstName())
                    .lastName(employee.getLastName())
                    .pesel(employee.getPesel())
                    .height(employee.getHeight())
                    .weight(employee.getWeight())
                    .email(employee.getEmail())
                    .employmentDate(employee.getEmploymentDate())
                    .currentPosition(employee.getCurrentPosition())
                    .currentSalary(employee.getCurrentSalary())
                    .numberOfPositions(employee.getPositions().size())
                    .build();
        } else if (person instanceof Retiree) {
            Retiree retiree = (Retiree) person;
            return RetireeDTO.builder()
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

    public Person toEntity(PersonDTO dto) {
        if (dto instanceof StudentDTO) {
            StudentDTO studentDTO = (StudentDTO) dto;
            Student student = new Student();
            student.setFirstName(studentDTO.getFirstName());
            student.setLastName(studentDTO.getLastName());
            student.setPesel(studentDTO.getPesel());
            student.setHeight(studentDTO.getHeight());
            student.setWeight(studentDTO.getWeight());
            student.setEmail(studentDTO.getEmail());
            student.setUniversityName(studentDTO.getUniversityName());
            student.setYearOfStudy(studentDTO.getYearOfStudy());
            student.setFieldOfStudy(studentDTO.getFieldOfStudy());
            student.setScholarship(studentDTO.getScholarship());
            return student;
        } else if (dto instanceof EmployeeDTO) {
            EmployeeDTO employeeDTO = (EmployeeDTO) dto;
            Employee employee = new Employee();
            employee.setFirstName(employeeDTO.getFirstName());
            employee.setLastName(employeeDTO.getLastName());
            employee.setPesel(employeeDTO.getPesel());
            employee.setHeight(employeeDTO.getHeight());
            employee.setWeight(employeeDTO.getWeight());
            employee.setEmail(employeeDTO.getEmail());
            employee.setEmploymentDate(employeeDTO.getEmploymentDate());
            employee.setCurrentPosition(employeeDTO.getCurrentPosition());
            employee.setCurrentSalary(employeeDTO.getCurrentSalary());
            return employee;
        } else if (dto instanceof RetireeDTO) {
            RetireeDTO retireeDTO = (RetireeDTO) dto;
            Retiree retiree = new Retiree();
            retiree.setFirstName(retireeDTO.getFirstName());
            retiree.setLastName(retireeDTO.getLastName());
            retiree.setPesel(retireeDTO.getPesel());
            retiree.setHeight(retireeDTO.getHeight());
            retiree.setWeight(retireeDTO.getWeight());
            retiree.setEmail(retireeDTO.getEmail());
            retiree.setPension(retireeDTO.getPension());
            retiree.setYearsWorked(retireeDTO.getYearsWorked());
            return retiree;
        }
        return null;
    }
}