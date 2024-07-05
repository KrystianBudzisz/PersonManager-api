package org.example.personmanagerapi.person.model;

import org.example.personmanagerapi.employee.EmployeeDTO;
import org.example.personmanagerapi.employee.model.Employee;
import org.example.personmanagerapi.position.model.PositionDTO;
import org.example.personmanagerapi.position.model.PositionMapper;
import org.example.personmanagerapi.rentiree.RetireeDTO;
import org.example.personmanagerapi.rentiree.model.Retiree;
import org.example.personmanagerapi.student.StudentDTO;
import org.example.personmanagerapi.student.model.Student;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.stream.Collectors;


@Component
public class PersonMapper {

    @Autowired
    private PositionMapper positionMapper;

    public PersonDTO toDTO(Person person) {
        if (person instanceof Student student) {
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
        } else if (person instanceof Employee employee) {
            Set<PositionDTO> positions = employee.getPositions().stream()
                    .map(positionMapper::toDTO)
                    .collect(Collectors.toSet());
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
                    .numberOfPositions(positions.size())
                    .positions(positions)
                    .build();
        } else if (person instanceof Retiree retiree) {
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
