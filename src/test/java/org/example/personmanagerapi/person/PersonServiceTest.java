package org.example.peoplehubapi.person;

import org.example.peoplehubapi.exception.InvalidPersonCreationException;
import org.example.peoplehubapi.person.model.CreatePersonCommand;
import org.example.peoplehubapi.person.model.PersonDTO;
import org.example.peoplehubapi.person.model.UpdatePersonCommand;
import org.example.peoplehubapi.person.specification.PersonSearchCriteria;
import org.example.peoplehubapi.person.specification.PersonSpecification;
import org.example.peoplehubapi.position.PositionRepository;
import org.example.peoplehubapi.position.model.CreatePositionCommand;
import org.example.peoplehubapi.position.model.Position;
import org.example.peoplehubapi.position.model.PositionDTO;
import org.example.peoplehubapi.strategy.creation.PersonCreationStrategy;
import org.example.peoplehubapi.strategy.model.Employee;
import org.example.peoplehubapi.strategy.model.Retiree;
import org.example.peoplehubapi.strategy.model.Student;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PersonServiceTest {

    @Mock
    private PersonRepository personRepository;

    @Mock
    private PositionRepository positionRepository;

    @Mock
    private PersonCreationStrategy studentStrategy;

    @Mock
    private PersonCreationStrategy employeeStrategy;

    @Mock
    private PersonCreationStrategy retireeStrategy;

    private PersonService personService;


    @BeforeEach
    void setUp() {
        when(studentStrategy.getType()).thenReturn("STUDENT");
        when(employeeStrategy.getType()).thenReturn("EMPLOYEE");
        when(retireeStrategy.getType()).thenReturn("RETIREE");

        List<PersonCreationStrategy> strategies = Arrays.asList(studentStrategy, employeeStrategy, retireeStrategy);

        personService = new PersonService(strategies, personRepository, positionRepository);
    }


    @Test
    void testCreateStudent() {
        Map<String, String> studentAttributes = new HashMap<>();
        studentAttributes.put("firstName", "John");
        studentAttributes.put("lastName", "Doe");
        studentAttributes.put("pesel", "99010112345");
        studentAttributes.put("email", "john.doe@example.com");
        studentAttributes.put("height", "180");
        studentAttributes.put("weight", "75");
        studentAttributes.put("universityName", "University of Technology");
        studentAttributes.put("yearOfStudy", "2");
        studentAttributes.put("fieldOfStudy", "Computer Science");
        studentAttributes.put("scholarship", "1000");

        CreatePersonCommand command = new CreatePersonCommand("STUDENT", studentAttributes);

        Student expectedStudent = new Student();
        expectedStudent.setFirstName("John");
        expectedStudent.setLastName("Doe");
        expectedStudent.setPesel("99010112345");
        expectedStudent.setEmail("john.doe@example.com");
        expectedStudent.setHeight(180.0);
        expectedStudent.setWeight(75.0);
        expectedStudent.setUniversityName("University of Technology");
        expectedStudent.setYearOfStudy(2);
        expectedStudent.setFieldOfStudy("Computer Science");
        expectedStudent.setScholarship(new BigDecimal("1000"));

        when(studentStrategy.create(any())).thenReturn(expectedStudent);
        when(personRepository.save(any(Student.class))).thenAnswer(invocation -> invocation.getArgument(0));

        personService.create(command);

        ArgumentCaptor<Student> studentCaptor = ArgumentCaptor.forClass(Student.class);
        verify(personRepository).save(studentCaptor.capture());
        Student savedStudent = studentCaptor.getValue();

        assertNotNull(savedStudent);
        assertEquals("John", savedStudent.getFirstName());
        assertEquals("Doe", savedStudent.getLastName());
        assertEquals("99010112345", savedStudent.getPesel());
        assertEquals("john.doe@example.com", savedStudent.getEmail());
        assertEquals(180, savedStudent.getHeight());
        assertEquals(75, savedStudent.getWeight());
        assertEquals("University of Technology", savedStudent.getUniversityName());
        assertEquals(2, savedStudent.getYearOfStudy());
        assertEquals("Computer Science", savedStudent.getFieldOfStudy());
        assertEquals(new BigDecimal("1000"), savedStudent.getScholarship());
    }


    @Test
    void testCreateEmployee() {
        Map<String, String> employeeAttributes = new HashMap<>();
        employeeAttributes.put("firstName", "Jane");
        employeeAttributes.put("lastName", "Doe");
        employeeAttributes.put("pesel", "89010112345");
        employeeAttributes.put("email", "jane.doe@example.com");
        employeeAttributes.put("height", "170");
        employeeAttributes.put("weight", "60");
        employeeAttributes.put("employmentDate", "2020-01-01");
        employeeAttributes.put("position", "Software Developer");
        employeeAttributes.put("salary", "3000");
        employeeAttributes.put("numberOfProfessions", "1");

        CreatePersonCommand command = new CreatePersonCommand("EMPLOYEE", employeeAttributes);

        Employee expectedEmployee = Employee.builder()
                .firstName("Jane")
                .lastName("Doe")
                .pesel("89010112345")
                .email("jane.doe@example.com")
                .height(170.0)
                .weight(60.0)
                .employmentDate(LocalDate.parse(employeeAttributes.get("employmentDate")))
                .position(employeeAttributes.get("position"))
                .salary(new BigDecimal(employeeAttributes.get("salary")))
                .numberOfProfessions(Integer.parseInt(employeeAttributes.get("numberOfProfessions")))
                .build();

        when(employeeStrategy.create(any(CreatePersonCommand.class))).thenReturn(expectedEmployee);
        when(personRepository.save(any(Employee.class))).thenAnswer(invocation -> invocation.getArgument(0));

        personService.create(command);

        ArgumentCaptor<Employee> employeeCaptor = ArgumentCaptor.forClass(Employee.class);
        verify(personRepository).save(employeeCaptor.capture());
        Employee savedEmployee = employeeCaptor.getValue();

        assertNotNull(savedEmployee);
        assertEquals("Jane", savedEmployee.getFirstName());
        assertEquals("Doe", savedEmployee.getLastName());
        assertEquals("89010112345", savedEmployee.getPesel());
        assertEquals("jane.doe@example.com", savedEmployee.getEmail());
        assertEquals(170.0, savedEmployee.getHeight());
        assertEquals(60.0, savedEmployee.getWeight());
        assertEquals(LocalDate.parse("2020-01-01"), savedEmployee.getEmploymentDate());
        assertEquals("Software Developer", savedEmployee.getPosition());
        assertEquals(new BigDecimal("3000"), savedEmployee.getSalary());
        assertEquals(1, savedEmployee.getNumberOfProfessions());
    }

    @Test
    void testCreateRetiree() {
        Map<String, String> retireeAttributes = new HashMap<>();
        retireeAttributes.put("firstName", "John");
        retireeAttributes.put("lastName", "Smith");
        retireeAttributes.put("pesel", "85010112345");
        retireeAttributes.put("email", "john.smith@example.com");
        retireeAttributes.put("height", "175");
        retireeAttributes.put("weight", "70");
        retireeAttributes.put("pensionAmount", "2000");
        retireeAttributes.put("yearsWorked", "30");

        CreatePersonCommand command = new CreatePersonCommand("RETIREE", retireeAttributes);

        Retiree expectedRetiree = Retiree.builder()
                .firstName("John")
                .lastName("Smith")
                .pesel("85010112345")
                .email("john.smith@example.com")
                .height(175.0)
                .weight(70.0)
                .pensionAmount(new BigDecimal("2000"))
                .yearsWorked(30)
                .build();

        when(retireeStrategy.create(any(CreatePersonCommand.class))).thenReturn(expectedRetiree);
        when(personRepository.save(any(Retiree.class))).thenAnswer(invocation -> invocation.getArgument(0));

        personService.create(command);

        ArgumentCaptor<Retiree> retireeCaptor = ArgumentCaptor.forClass(Retiree.class);
        verify(personRepository).save(retireeCaptor.capture());
        Retiree savedRetiree = retireeCaptor.getValue();

        assertNotNull(savedRetiree);
        assertEquals("John", savedRetiree.getFirstName());
        assertEquals("Smith", savedRetiree.getLastName());
        assertEquals("85010112345", savedRetiree.getPesel());
        assertEquals("john.smith@example.com", savedRetiree.getEmail());
        assertEquals(175.0, savedRetiree.getHeight());
        assertEquals(70.0, savedRetiree.getWeight());
        assertEquals(new BigDecimal("2000"), savedRetiree.getPensionAmount());
        assertEquals(30, savedRetiree.getYearsWorked());
    }

    @Test
    void testUpdateRetiree() {
        Map<String, String> retireeAttributes = new HashMap<>();
        retireeAttributes.put("firstName", "John");
        retireeAttributes.put("lastName", "Smith");
        retireeAttributes.put("email", "john.smith@example.com");
        retireeAttributes.put("height", "180");
        retireeAttributes.put("weight", "75");
        retireeAttributes.put("pensionAmount", "2500");
        retireeAttributes.put("yearsWorked", "35");

        UpdatePersonCommand command = new UpdatePersonCommand(retireeAttributes);

        Retiree mockRetiree = Retiree.builder()
                .firstName("John")
                .lastName("Doe")
                .email("john.doe@example.com")
                .height(175.0)
                .weight(70.0)
                .pensionAmount(new BigDecimal("2000"))
                .yearsWorked(30)
                .build();

        when(personRepository.findById(anyLong())).thenReturn(Optional.of(mockRetiree));

        personService.update(1L, command);

        ArgumentCaptor<Retiree> retireeCaptor = ArgumentCaptor.forClass(Retiree.class);
        verify(personRepository).save(retireeCaptor.capture());

    }


    @Test
    public void whenAddPositionToEmployee_thenPositionIsAddedCorrectly() {
        Long employeeId = 1L;
        CreatePositionCommand positionCommand = new CreatePositionCommand();
        positionCommand.setName("Developer");
        positionCommand.setSalary(new BigDecimal("70000"));
        positionCommand.setStartDate(LocalDate.of(2023, 1, 1));
        positionCommand.setEndDate(LocalDate.of(2023, 12, 31));

        Employee employee = new Employee();
        employee.setId(employeeId);
        when(personRepository.findById(employeeId)).thenReturn(Optional.of(employee));
        when(positionRepository.findByEmployeeIdAndStartDateLessThanEqualAndEndDateGreaterThanEqual(employeeId, positionCommand.getEndDate(), positionCommand.getStartDate()))
                .thenReturn(Collections.emptyList());

        Position newPosition = new Position();
        when(positionRepository.save(any(Position.class))).thenReturn(newPosition);

        PositionDTO addedPosition = personService.addPosition(employeeId, positionCommand);

        assertNotNull(addedPosition);
        assertEquals("Developer", addedPosition.getName());
        assertEquals(new BigDecimal("70000"), addedPosition.getSalary());
        assertEquals(LocalDate.of(2023, 1, 1), addedPosition.getStartDate());
        assertEquals(LocalDate.of(2023, 12, 31), addedPosition.getEndDate());
        assertEquals(employeeId, addedPosition.getEmployeeId());
    }

    @Test
    public void testSearchEmployeeByValues() {
        Employee employee = new Employee();
        employee.setId(1L);
        employee.setFirstName("John");
        employee.setLastName("Doe");
        employee.setPesel("12345678901");
        employee.setEmail("john.doe@example.com");
        employee.setHeight(180.0);
        employee.setWeight(80.0);
        employee.setEmploymentDate(LocalDate.of(2020, 1, 1));
        employee.setPosition("Software Developer");
        employee.setSalary(new BigDecimal("5000"));
        employee.setNumberOfProfessions(1);

        when(personRepository.findAll(any(PersonSpecification.class), any(Pageable.class)))
                .thenReturn(new PageImpl<>(Collections.singletonList(employee)));

        PersonSearchCriteria searchCriteria = new PersonSearchCriteria();
        searchCriteria.setType("EMPLOYEE");
        searchCriteria.setName("John");
        searchCriteria.setLastName("Doe");
        searchCriteria.setPesel("12345678901");
        searchCriteria.setEmail("john.doe@example.com");
        searchCriteria.setHeightFrom(175.0);
        searchCriteria.setHeightTo(185.0);
        searchCriteria.setWeightFrom(75.0);
        searchCriteria.setWeightTo(85.0);
        searchCriteria.setEmploymentDateFrom(LocalDate.of(2020, 1, 1));
        searchCriteria.setEmploymentDateTo(LocalDate.of(2022, 1, 1));
        searchCriteria.setPosition("Software Developer");
        searchCriteria.setSalaryFrom(new BigDecimal("4000"));
        searchCriteria.setSalaryTo(new BigDecimal("6000"));
        searchCriteria.setNumberOfProfessionsFrom(1);
        searchCriteria.setNumberOfProfessionsTo(3);

        Pageable pageable = PageRequest.of(0, 10);

        Page<PersonDTO> resultPage = personService.searchPersons(searchCriteria, pageable);
        List<PersonDTO> result = resultPage.getContent();

        assertEquals(1, result.size());
        PersonDTO foundEmployee = result.get(0);
        assertEquals(employee.getFirstName(), foundEmployee.getFirstName());
        assertEquals(employee.getLastName(), foundEmployee.getLastName());
        assertEquals(employee.getPesel(), foundEmployee.getPesel());
        assertEquals(employee.getHeight(), foundEmployee.getHeight());
        assertEquals(employee.getWeight(), foundEmployee.getWeight());
        assertEquals(employee.getEmail(), foundEmployee.getEmail());
        assertEquals(employee.getEmploymentDate(), foundEmployee.getAdditionalAttributes().get("employmentDate"));
        assertEquals(employee.getPosition(), foundEmployee.getAdditionalAttributes().get("position"));
        assertEquals(employee.getSalary(), foundEmployee.getAdditionalAttributes().get("salary"));
        assertEquals(employee.getNumberOfProfessions(), foundEmployee.getAdditionalAttributes().get("numberOfProfessions"));
    }

    @Test
    void createEmployee_MissingFirstName_ThrowsInvalidPersonCreationException() {
        // Arrange
        Map<String, String> params = new HashMap<>();
        params.put("lastName", "Doe");
        params.put("pesel", "89010112345");
        params.put("email", "jane.doe@example.com");
        params.put("height", "170");
        params.put("weight", "60");
        params.put("employmentDate", "2020-01-01");
        params.put("position", "Software Developer");
        params.put("salary", "3000");
        params.put("numberOfProfessions", "1");

        CreatePersonCommand command = new CreatePersonCommand("EMPLOYEE", params);

        doThrow(new InvalidPersonCreationException("First name is required."))
                .when(employeeStrategy).create(any(CreatePersonCommand.class));

        Exception exception = assertThrows(InvalidPersonCreationException.class, () -> personService.create(command));

        assertTrue(exception.getMessage().contains("First name is required."));
    }
}

