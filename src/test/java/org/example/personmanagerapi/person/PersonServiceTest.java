package org.example.personmanagerapi.person;


import org.example.personmanagerapi.exception.DuplicatePersonException;
import org.example.personmanagerapi.exception.InvalidPersonTypeException;
import org.example.personmanagerapi.exception.PersonNotFoundException;
import org.example.personmanagerapi.person.model.Person;
import org.example.personmanagerapi.person.model.PersonCommand;
import org.example.personmanagerapi.person.model.PersonDTO;
import org.example.personmanagerapi.person.model.PersonMapper;
import org.example.personmanagerapi.person.search.PersonSearchCriteria;
import org.example.personmanagerapi.strategy.PersonTypeStrategy;
import org.example.personmanagerapi.strategy.PersonTypeStrategyFactory;
import org.example.personmanagerapi.student.StudentDTO;
import org.example.personmanagerapi.student.model.Student;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PersonServiceTest {

    @InjectMocks
    private PersonService personService;

    @Mock
    private PersonRepository personRepository;

    @Mock
    private PersonMapper personMapper;

    @Mock
    private PersonTypeStrategyFactory strategyFactory;

    @Captor
    private ArgumentCaptor<Person> personArgumentCaptor;

    private Student student;
    private PersonCommand personCommand;
    private StudentDTO studentDTO;


    @BeforeEach
    public void setUp() {
        student = new Student();
        student.setId(UUID.randomUUID());
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

        personCommand = new PersonCommand();
        personCommand.setType("student");
        personCommand.setFirstName("John");
        personCommand.setLastName("Doe");
        personCommand.setPesel(student.getPesel());
        personCommand.setHeight(180.5);
        personCommand.setWeight(75.0);
        personCommand.setEmail("john.doe@example.com");
        Map<String, Object> typeSpecificFields = new HashMap<>();
        typeSpecificFields.put("universityName", "XYZ University");
        typeSpecificFields.put("yearOfStudy", 3);
        typeSpecificFields.put("fieldOfStudy", "Computer Science");
        typeSpecificFields.put("scholarship", 1000.0);
        personCommand.setTypeSpecificFields(typeSpecificFields);

        studentDTO = new StudentDTO();
        studentDTO.setId(student.getId());
        studentDTO.setFirstName("John");
        studentDTO.setLastName("Doe");
        studentDTO.setPesel(student.getPesel());
        studentDTO.setHeight(180.5);
        studentDTO.setWeight(75.0);
        studentDTO.setEmail("john.doe@example.com");
        studentDTO.setUniversityName("XYZ University");
        studentDTO.setYearOfStudy(3);
        studentDTO.setFieldOfStudy("Computer Science");
        studentDTO.setScholarship(1000.0);

    }

    @Test
    public void getPersonById_ShouldReturnPerson() {
        when(personRepository.findById(student.getId())).thenReturn(Optional.of(student));
        when(personMapper.toDTO(any(Person.class))).thenReturn(studentDTO);

        PersonDTO result = personService.getPersonById(student.getId());

        assertThat(result).isNotNull();
        assertThat(result.getFirstName()).isEqualTo(student.getFirstName());
        verify(personRepository).findById(student.getId());
        verify(personMapper).toDTO(any(Person.class));
    }

    @Test
    public void getPersonById_ShouldThrowPersonNotFoundException() {
        when(personRepository.findById(student.getId())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> personService.getPersonById(student.getId()))
                .isInstanceOf(PersonNotFoundException.class)
                .hasMessageContaining("Person not found with ID: " + student.getId());

        verify(personRepository).findById(student.getId());
    }

    @Test
    public void createPerson_ShouldReturnCreatedPerson() {
        PersonTypeStrategy strategy = mock(PersonTypeStrategy.class);
        when(strategyFactory.getStrategy("student")).thenReturn(strategy);
        when(strategy.preparePerson(personCommand)).thenReturn(student);
        when(personRepository.save(any(Person.class))).thenReturn(student);
        when(personMapper.toDTO(any(Person.class))).thenReturn(studentDTO);

        PersonDTO result = personService.createPerson(personCommand);

        assertThat(result).isNotNull();
        assertThat(result.getFirstName()).isEqualTo(student.getFirstName());
        verify(personRepository).save(personArgumentCaptor.capture());
        verify(personMapper).toDTO(any(Person.class));

        Person capturedPerson = personArgumentCaptor.getValue();
        assertThat(capturedPerson).isNotNull();
        assertThat(capturedPerson.getFirstName()).isEqualTo("John");
        assertThat(capturedPerson.getLastName()).isEqualTo("Doe");
    }

    @Test
    public void createPerson_ShouldThrowDuplicatePersonException() {
        PersonTypeStrategy strategy = mock(PersonTypeStrategy.class);
        when(strategyFactory.getStrategy("student")).thenReturn(strategy);
        when(strategy.preparePerson(personCommand)).thenReturn(student);
        when(personRepository.save(any(Person.class))).thenThrow(DataIntegrityViolationException.class);

        assertThatThrownBy(() -> personService.createPerson(personCommand))
                .isInstanceOf(DuplicatePersonException.class)
                .hasMessageContaining("A person with the same unique identifier already exists.");

        verify(personRepository).save(any(Person.class));
    }

    @Test
    public void updatePerson_ShouldReturnUpdatedPerson() {
        PersonTypeStrategy strategy = mock(PersonTypeStrategy.class);
        when(personRepository.findById(student.getId())).thenReturn(Optional.of(student));
        when(strategyFactory.getStrategy("student")).thenReturn(strategy);
        when(strategy.preparePerson(personCommand)).thenReturn(student);
        when(personRepository.save(any(Person.class))).thenReturn(student);
        when(personMapper.toDTO(any(Person.class))).thenReturn(studentDTO);

        PersonDTO result = personService.updatePerson(student.getId(), personCommand);

        assertThat(result).isNotNull();
        assertThat(result.getFirstName()).isEqualTo("John");
        verify(personRepository).findById(student.getId());
        verify(personRepository).save(personArgumentCaptor.capture());
        verify(personMapper).toDTO(any(Person.class));

        Person capturedPerson = personArgumentCaptor.getValue();
        assertThat(capturedPerson).isNotNull();
        assertThat(capturedPerson.getFirstName()).isEqualTo("John");
        assertThat(capturedPerson.getLastName()).isEqualTo("Doe");
    }

    @Test
    public void updatePerson_ShouldThrowPersonNotFoundException() {
        when(personRepository.findById(student.getId())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> personService.updatePerson(student.getId(), personCommand))
                .isInstanceOf(PersonNotFoundException.class)
                .hasMessageContaining("Person not found with ID: " + student.getId());

        verify(personRepository).findById(student.getId());
    }

    @Test
    public void updatePerson_ShouldThrowInvalidPersonTypeException() {
        when(personRepository.findById(student.getId())).thenReturn(Optional.of(student));
        when(strategyFactory.getStrategy("student")).thenReturn(null);

        assertThatThrownBy(() -> personService.updatePerson(student.getId(), personCommand))
                .isInstanceOf(InvalidPersonTypeException.class)
                .hasMessageContaining("Person type not supported: student");

        verify(personRepository).findById(student.getId());
    }

    @Test
    public void searchPersons_ShouldReturnPersonPage() {
        PersonSearchCriteria criteria = new PersonSearchCriteria();
        Pageable pageable = PageRequest.of(0, 10);
        List<Person> personList = Collections.singletonList(student);
        Page<Person> personPage = new PageImpl<>(personList);

        when(personRepository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(personPage);
        when(personMapper.toDTO(any(Person.class))).thenReturn(studentDTO);

        Page<PersonDTO> result = personService.searchPersons(criteria, pageable);

        assertThat(result).isNotNull();
        assertThat(result.getTotalElements()).isEqualTo(1);
        verify(personRepository).findAll(any(Specification.class), any(Pageable.class));
        verify(personMapper).toDTO(any(Person.class));
    }

    @Test
    public void searchPersons_ShouldReturnEmptyPage() {
        PersonSearchCriteria criteria = new PersonSearchCriteria();
        Pageable pageable = PageRequest.of(0, 10);
        List<Person> personList = Collections.emptyList();
        Page<Person> personPage = new PageImpl<>(personList);

        when(personRepository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(personPage);

        Page<PersonDTO> result = personService.searchPersons(criteria, pageable);

        assertThat(result).isNotNull();
        assertThat(result.getTotalElements()).isEqualTo(0);
        verify(personRepository).findAll(any(Specification.class), any(Pageable.class));
    }


}