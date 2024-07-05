package org.example.personmanagerapi.person;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.personmanagerapi.employee.model.Employee;
import org.example.personmanagerapi.person.model.PersonCommand;
import org.example.personmanagerapi.position.PositionRepository;
import org.example.personmanagerapi.position.model.Position;
import org.example.personmanagerapi.position.model.PositionCommand;
import org.example.personmanagerapi.student.model.Student;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class PersonControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PersonRepository personRepository;

    @Autowired
    private PositionRepository positionRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @AfterEach
    public void tearDown() {
        positionRepository.deleteAll();
        personRepository.deleteAll();
    }

    @Test
    @WithMockUser(roles = {"ADMIN", "EMPLOYEE"})
    @Transactional
    public void addPositionToEmployee_ShouldReturnUpdatedEmployee() throws Exception {
        Employee employee = new Employee();
        employee.setFirstName("Jane");
        employee.setLastName("Doe");
        employee.setPesel(UUID.randomUUID().toString().substring(0, 11));
        employee.setHeight(165.0);
        employee.setWeight(60.0);
        employee.setEmail("jane.doe@example.com");
        employee.setEmploymentDate("2021-01-01");
        employee.setCurrentPosition("Developer");
        employee.setCurrentSalary(50000.0);
        employee.setVersion(0L);
        employee = personRepository.save(employee);

        PositionCommand positionCommand = new PositionCommand();
        positionCommand.setPositionName("Senior Developer");
        positionCommand.setSalary(60000.0);
        positionCommand.setStartDate(LocalDate.of(2024, 1, 1));
        positionCommand.setEndDate(LocalDate.of(2025, 1, 1));

        mockMvc.perform(post("/api/persons/" + employee.getId() + "/position")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(positionCommand)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.firstName").value("Jane"))
                .andExpect(jsonPath("$.lastName").value("Doe"))
                .andExpect(jsonPath("$.pesel").value(employee.getPesel()))
                .andExpect(jsonPath("$.height").value(165.0))
                .andExpect(jsonPath("$.weight").value(60.0))
                .andExpect(jsonPath("$.email").value("jane.doe@example.com"))
                .andExpect(jsonPath("$.currentPosition").value("Senior Developer"))
                .andExpect(jsonPath("$.currentSalary").value(60000.0))
                .andExpect(jsonPath("$.positions[0].positionName").value("Senior Developer"))
                .andExpect(jsonPath("$.positions[0].salary").value(60000.0))
                .andExpect(jsonPath("$.positions[0].startDate").value("2024-01-01"))
                .andExpect(jsonPath("$.positions[0].endDate").value("2025-01-01"));

        Optional<Employee> updatedEmployeeOptional = personRepository.findById(employee.getId())
                .map(person -> (Employee) person);

        assertThat(updatedEmployeeOptional).isPresent();
        Employee updatedEmployee = updatedEmployeeOptional.get();

        updatedEmployee.getPositions().size();

        assertThat(updatedEmployee.getCurrentPosition()).isEqualTo("Senior Developer");
        assertThat(updatedEmployee.getCurrentSalary()).isEqualTo(60000.0);
        assertThat(updatedEmployee.getPositions()).hasSize(1);
        Position addedPosition = updatedEmployee.getPositions().iterator().next();
        assertThat(addedPosition.getPositionName()).isEqualTo("Senior Developer");
        assertThat(addedPosition.getSalary()).isEqualTo(60000.0);
        assertThat(addedPosition.getStartDate()).isEqualTo(LocalDate.of(2024, 1, 1));
        assertThat(addedPosition.getEndDate()).isEqualTo(LocalDate.of(2025, 1, 1));
    }


    @Test
    @WithMockUser(roles = "ADMIN")
    public void createPerson_ShouldReturnCreatedPerson() throws Exception {
        PersonCommand command = new PersonCommand();
        command.setType("student");
        command.setFirstName("John");
        command.setLastName("Doe");
        command.setPesel(UUID.randomUUID().toString().substring(0, 11));
        command.setHeight(180.5);
        command.setWeight(75.0);
        command.setEmail("john.doe@example.com");
        Map<String, Object> typeSpecificFields = new HashMap<>();
        typeSpecificFields.put("universityName", "XYZ University");
        typeSpecificFields.put("yearOfStudy", 3);
        typeSpecificFields.put("fieldOfStudy", "Computer Science");
        typeSpecificFields.put("scholarship", 1000.0);
        command.setTypeSpecificFields(typeSpecificFields);

        mockMvc.perform(post("/api/persons")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(command)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.firstName").value("John"))
                .andExpect(jsonPath("$.lastName").value("Doe"))
                .andExpect(jsonPath("$.pesel").value(command.getPesel()))
                .andExpect(jsonPath("$.height").value(180.5))
                .andExpect(jsonPath("$.weight").value(75.0))
                .andExpect(jsonPath("$.email").value("john.doe@example.com"))
                .andExpect(jsonPath("$.universityName").value("XYZ University"))
                .andExpect(jsonPath("$.yearOfStudy").value(3))
                .andExpect(jsonPath("$.fieldOfStudy").value("Computer Science"))
                .andExpect(jsonPath("$.scholarship").value(1000.0));
    }


    @Test
    @WithMockUser(roles = {"ADMIN", "EMPLOYEE"})
    public void removePositionFromEmployee_ShouldReturnNoContent() throws Exception {
        Employee employee = new Employee();
        employee.setFirstName("Jane");
        employee.setLastName("Doe");
        employee.setPesel(UUID.randomUUID().toString().substring(0, 11));
        employee.setHeight(165.0);
        employee.setWeight(60.0);
        employee.setEmail("jane.doe@example.com");
        employee.setEmploymentDate("2021-01-01");
        employee.setCurrentPosition("Developer");
        employee.setCurrentSalary(50000.0);
        employee.setVersion(0L);
        employee = personRepository.save(employee);

        Position position = new Position();
        position.setPositionName("Developer");
        position.setSalary(50000.0);
        position.setStartDate(LocalDate.of(2021, 1, 1));
        position.setEndDate(LocalDate.of(2023, 1, 1));
        position.setEmployee(employee);
        position = positionRepository.save(position);

        mockMvc.perform(delete("/api/persons/" + employee.getId() + "/position/" + position.getId()))
                .andExpect(status().isNoContent());

        Optional<Position> deletedPositionOptional = positionRepository.findById(position.getId());
        assertThat(deletedPositionOptional).isNotPresent();
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void updatePerson_ShouldReturnUpdatedPerson() throws Exception {
        Student student = new Student();
        student.setFirstName("John");
        student.setLastName("Doe");
        student.setPesel(UUID.randomUUID().toString().substring(0, 11));
        student.setHeight(180.5);
        student.setWeight(75.0);
        student.setEmail("john.doe@example.com");
        student.setUniversityName("XYZ University");
        student.setYearOfStudy(3);
        student.setFieldOfStudy("Computer Science");
        student.setScholarship(1000.0);
        student.setVersion(0L);
        student = personRepository.save(student);

        PersonCommand command = new PersonCommand();
        command.setType("student");
        command.setFirstName("Johnny");
        command.setLastName("Doe Updated");
        command.setPesel(student.getPesel());
        command.setHeight(180.5);
        command.setWeight(75.0);
        command.setEmail("john.doe@example.com");
        Map<String, Object> typeSpecificFields = new HashMap<>();
        typeSpecificFields.put("universityName", "XYZ University");
        typeSpecificFields.put("yearOfStudy", 3);
        typeSpecificFields.put("fieldOfStudy", "Computer Science");
        typeSpecificFields.put("scholarship", 1000.0);
        command.setTypeSpecificFields(typeSpecificFields);

        mockMvc.perform(put("/api/persons/" + student.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(command)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("Johnny"))
                .andExpect(jsonPath("$.lastName").value("Doe Updated"))
                .andExpect(jsonPath("$.pesel").value(student.getPesel()))
                .andExpect(jsonPath("$.height").value(180.5))
                .andExpect(jsonPath("$.weight").value(75.0))
                .andExpect(jsonPath("$.email").value("john.doe@example.com"))
                .andExpect(jsonPath("$.universityName").value("XYZ University"))
                .andExpect(jsonPath("$.yearOfStudy").value(3))
                .andExpect(jsonPath("$.fieldOfStudy").value("Computer Science"))
                .andExpect(jsonPath("$.scholarship").value(1000.0));

        Optional<Student> updatedStudentOptional = personRepository.findById(student.getId())
                .map(person -> (Student) person);

        assertThat(updatedStudentOptional).isPresent();
        Student updatedStudent = updatedStudentOptional.get();
        assertThat(updatedStudent.getFirstName()).isEqualTo("Johnny");
        assertThat(updatedStudent.getLastName()).isEqualTo("Doe Updated");
    }
}