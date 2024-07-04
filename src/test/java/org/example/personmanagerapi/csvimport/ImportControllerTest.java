package org.example.peoplehubapi.csvimport;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.example.peoplehubapi.csvImport.model.ImportStatusDTO;
import org.example.peoplehubapi.person.PersonRepository;
import org.example.peoplehubapi.person.model.Person;
import org.example.peoplehubapi.strategy.model.Employee;
import org.example.peoplehubapi.strategy.model.Retiree;
import org.example.peoplehubapi.strategy.model.Student;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@AutoConfigureMockMvc
public class ImportControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PersonRepository personRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    public void setup() {
        objectMapper.registerModule(new JavaTimeModule());
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    public void testImportPersons() throws Exception {
        StringBuilder csvData = new StringBuilder();
        csvData.append("STUDENT,John,Doe,1234567890,180.0,80.0,john.doe@example.com,Test University,2,Test Field,1000\n");
        csvData.append("EMPLOYEE,Jane,Smith,9876543210,160.0,70.0,jane.smith@example.com,2020-01-01,Manager,5000,20\n");
        csvData.append("RETIREE,James,Brown,1357924680,170.0,75.0,james.brown@example.com,2000,30\n");

        MockMultipartFile file = new MockMultipartFile("file", "test.csv",
                MediaType.TEXT_PLAIN_VALUE, csvData.toString().getBytes());

        MvcResult result = mockMvc.perform(multipart("/api/imports/persons")
                        .file(file))
                .andExpect(status().isAccepted())
                .andReturn();

        System.out.println("Response Body: " + result.getResponse().getContentAsString());

        List<Person> importedPersons = personRepository.findAll();
        assertEquals(2, importedPersons.size());

        for (Person person : importedPersons) {
            assertNotNull(person.getId());
            assertNotNull(person.getFirstName());
            assertNotNull(person.getLastName());
            assertNotNull(person.getPesel());
            assertNotNull(person.getHeight());
            assertNotNull(person.getWeight());
            assertNotNull(person.getEmail());

            if (person instanceof Student) {
                Student student = (Student) person;
                assertNotNull(student.getUniversityName());
                assertNotNull(student.getYearOfStudy());
                assertNotNull(student.getFieldOfStudy());
                assertNotNull(student.getScholarship());
            } else if (person instanceof Employee) {
                Employee employee = (Employee) person;
                assertNotNull(employee.getEmploymentDate());
                assertNotNull(employee.getPosition());
                assertNotNull(employee.getSalary());
                assertNotNull(employee.getNumberOfProfessions());
            } else if (person instanceof Retiree) {
                Retiree retiree = (Retiree) person;
                assertNotNull(retiree.getPensionAmount());
                assertNotNull(retiree.getYearsWorked());
            }
        }
    }


    @Test
    @WithMockUser(roles = {"ADMIN"})
    public void testImportEmployeesAndCheckCompletedStatus() throws Exception {
        MockMultipartFile file = new MockMultipartFile("file", "employees.csv", "text/csv",
                "EMPLOYEE,John,Doe,12345678901,180,80,john.doe@example.com,2022-01-01,Software Engineer,50000,1\n".getBytes());

        MvcResult importResult = mockMvc.perform(multipart("/api/imports/persons")
                        .file(file))
                .andExpect(status().isAccepted())
                .andReturn();


        String responseString = importResult.getResponse().getContentAsString();
        ImportStatusDTO importStatusDTO = objectMapper.readValue(responseString, ImportStatusDTO.class);
        Long importId = importStatusDTO.getId();


        Thread.sleep(1000);


        mockMvc.perform(get("/api/imports/status/" + importId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is("COMPLETED")));
    }


}
