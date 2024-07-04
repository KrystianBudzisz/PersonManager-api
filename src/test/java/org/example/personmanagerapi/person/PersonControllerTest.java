package org.example.peoplehubapi.person;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.peoplehubapi.person.model.CreatePersonCommand;
import org.example.peoplehubapi.person.model.Person;
import org.example.peoplehubapi.person.model.UpdatePersonCommand;
import org.example.peoplehubapi.position.PositionRepository;
import org.example.peoplehubapi.position.model.Position;
import org.example.peoplehubapi.strategy.model.Employee;
import org.example.peoplehubapi.strategy.model.Student;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class PersonControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private PersonRepository personRepository;

    @Autowired
    private PositionRepository positionRepository;


    @AfterEach
    public void After() {
        personRepository.deleteAll();
        positionRepository.deleteAll();
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    public void createPerson_ShouldPersistAndReturnAllData() throws Exception {
        Map<String, String> params = new HashMap<>();
        params.put("firstName", "John");
        params.put("lastName", "Doe");
        params.put("pesel", "12345678901");
        params.put("height", "180");
        params.put("weight", "80");
        params.put("email", "john.doe@example.com");
        params.put("universityName", "Example University");
        params.put("scholarship", "1500.0");
        params.put("yearOfStudy", "2");
        params.put("fieldOfStudy", "Computer Science");

        CreatePersonCommand command = new CreatePersonCommand("STUDENT", params);

        mockMvc.perform(post("/api/persons")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(command)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.firstName", is("John")))
                .andExpect(jsonPath("$.lastName", is("Doe")))
                .andExpect(jsonPath("$.pesel", is("12345678901")))
                .andExpect(jsonPath("$.height", is(180.0)))
                .andExpect(jsonPath("$.weight", is(80.0)))
                .andExpect(jsonPath("$.email", is("john.doe@example.com")));

        Optional<Person> savedPersonOpt = personRepository.findAll().stream().findFirst();
        assertTrue(savedPersonOpt.isPresent(), "Person was not saved in the database");
        Person savedPerson = savedPersonOpt.get();

        assertEquals("John", savedPerson.getFirstName());
        assertEquals("Doe", savedPerson.getLastName());
        assertEquals("12345678901", savedPerson.getPesel());
        assertEquals(180.0, savedPerson.getHeight());
        assertEquals(80.0, savedPerson.getWeight());
        assertEquals("john.doe@example.com", savedPerson.getEmail());
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    public void testCreateAndUpdatePersonWithAllAttributes() throws Exception {
        Map<String, String> studentAttributes = new HashMap<>();
        studentAttributes.put("firstName", "John");
        studentAttributes.put("lastName", "Doe");
        studentAttributes.put("pesel", "12345678901");
        studentAttributes.put("height", "180");
        studentAttributes.put("weight", "80");
        studentAttributes.put("email", "john.doe@example.com");
        studentAttributes.put("universityName", "Example University");
        studentAttributes.put("yearOfStudy", "2");
        studentAttributes.put("fieldOfStudy", "Computer Science");
        studentAttributes.put("scholarship", "1000");

        CreatePersonCommand createCommand = new CreatePersonCommand("STUDENT", studentAttributes);

        mockMvc.perform(post("/api/persons")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createCommand)))
                .andExpect(status().isCreated());

        Long personId = personRepository.findAll().get(0).getId();

        Map<String, String> updatedAttributes = new HashMap<>(studentAttributes);
        updatedAttributes.put("firstName", "Jane");
        updatedAttributes.put("email", "jane.doe@example.com");
        updatedAttributes.put("universityName", "Updated University");
        updatedAttributes.put("yearOfStudy", "3");
        updatedAttributes.put("fieldOfStudy", "Artificial Intelligence");
        updatedAttributes.put("scholarship", "1500");

        UpdatePersonCommand updateCommand = new UpdatePersonCommand(updatedAttributes);

        mockMvc.perform(put("/api/persons/{id}", personId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateCommand)))
                .andExpect(status().isOk());

        Optional<Person> updatedPersonOpt = personRepository.findById(personId);
        assertTrue(updatedPersonOpt.isPresent());
        Person updatedPerson = updatedPersonOpt.get();
        assertTrue(updatedPerson instanceof Student);
        Student updatedStudent = (Student) updatedPerson;

        assertEquals("Jane", updatedStudent.getFirstName());
        assertEquals("jane.doe@example.com", updatedStudent.getEmail());
        assertEquals("Updated University", updatedStudent.getUniversityName());
        assertEquals(3, updatedStudent.getYearOfStudy());
        assertEquals("Artificial Intelligence", updatedStudent.getFieldOfStudy());
        assertEquals(0, updatedStudent.getScholarship().compareTo(new BigDecimal("1500")), "Scholarship amount does not match expected value.");

    }

    @Test
    @WithMockUser(roles = {"ADMIN", "EMPLOYEE"})
    public void testAddPositionToEmployee() throws Exception {
        Employee testEmployee = Employee.builder()
                .firstName("John")
                .lastName("Doe")
                .pesel("uniquePesel123")
                .email("john.doe@example.com")
                .employmentDate(LocalDate.now())
                .salary(BigDecimal.valueOf(50000))
                .numberOfProfessions(1)
                .build();
        testEmployee = personRepository.save(testEmployee);
        String positionJson = """
                {
                    "name": "Software Engineer",
                    "salary": 70000,
                    "startDate": "2023-01-01",
                    "endDate": "2023-12-31"
                }
                """;

        mockMvc.perform(post("/api/persons/{employeeId}/positions", testEmployee.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(positionJson))
                .andExpect(status().isCreated());

        List<Position> positions = positionRepository.findByEmployeeId(testEmployee.getId());
        assertEquals(1, positions.size());

        Position addedPosition = positions.get(0);
        assertNotNull(addedPosition);
        assertEquals("Software Engineer", addedPosition.getName());
        assertEquals(0, BigDecimal.valueOf(70000).compareTo(addedPosition.getSalary()));
        assertEquals(LocalDate.of(2023, 1, 1), addedPosition.getStartDate());
        assertEquals(LocalDate.of(2023, 12, 31), addedPosition.getEndDate());
        assertEquals(testEmployee.getId(), addedPosition.getEmployee().getId());
    }

    @Test
    @WithMockUser(roles = {"USER"})
    public void searchPersons_WhenCriteriaMatch_ShouldReturnSpecificPerson() throws Exception {
        Employee testEmployee = Employee.builder()
                .firstName("John")
                .lastName("Doe")
                .pesel("uniquePesel123")
                .email("john.doe@example.com")
                .employmentDate(LocalDate.now())
                .salary(BigDecimal.valueOf(50000))
                .numberOfProfessions(1)
                .build();
        personRepository.save(testEmployee);

        mockMvc.perform(get("/api/persons/search")
                        .param("name", "John")
                        .param("lastName", "Doe")
                        .param("pesel", "uniquePesel123")
                        .param("email", "john.doe@example.com")
                        .param("salaryFrom", "40000")
                        .param("salaryTo", "60000")
                        .param("employmentDateFrom", LocalDate.now().minusDays(1).toString())
                        .param("employmentDateTo", LocalDate.now().plusDays(1).toString())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1))) // Oczekuj dokładnie jednej pasującej osoby
                .andExpect(jsonPath("$.content[0].firstName", is("John")))
                .andExpect(jsonPath("$.content[0].lastName", is("Doe")))
                .andExpect(jsonPath("$.content[0].pesel", is("uniquePesel123")))
                .andExpect(jsonPath("$.content[0].email", is("john.doe@example.com")));
    }


}

