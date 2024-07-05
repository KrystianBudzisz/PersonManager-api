package org.example.personmanagerapi.position;

import org.example.personmanagerapi.employee.model.Employee;
import org.example.personmanagerapi.exception.InvalidPersonTypeException;
import org.example.personmanagerapi.exception.PersonNotFoundException;
import org.example.personmanagerapi.exception.PositionNotFoundException;
import org.example.personmanagerapi.exception.PositionOverlapException;
import org.example.personmanagerapi.person.PersonRepository;
import org.example.personmanagerapi.person.model.Person;
import org.example.personmanagerapi.position.model.Position;
import org.example.personmanagerapi.position.model.PositionCommand;
import org.example.personmanagerapi.position.model.PositionMapper;
import org.example.personmanagerapi.student.model.Student;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class PositionServiceTest {

    @InjectMocks
    private PositionService positionService;

    @Mock
    private PersonRepository personRepository;

    @Mock
    private PositionRepository positionRepository;

    @Mock
    private PositionMapper positionMapper;

    @Captor
    private ArgumentCaptor<Position> positionArgumentCaptor;

    private Employee employee;
    private PositionCommand positionCommand;
    private Position position;

    @BeforeEach
    public void setUp() {
        employee = new Employee();
        employee.setId(UUID.randomUUID());
        employee.setFirstName("John");
        employee.setLastName("Doe");
        employee.setPesel(UUID.randomUUID().toString().substring(0, 11));
        employee.setHeight(180.5);
        employee.setWeight(75.0);
        employee.setEmail("john.doe@example.com");
        employee.setEmploymentDate("2021-01-01");
        employee.setCurrentPosition("Developer");
        employee.setCurrentSalary(50000.0);

        positionCommand = new PositionCommand();
        positionCommand.setPositionName("Senior Developer");
        positionCommand.setSalary(60000.0);
        positionCommand.setStartDate(LocalDate.of(2022, 1, 1));
        positionCommand.setEndDate(LocalDate.of(2024, 1, 1));

        position = new Position();
        position.setId(1L);
        position.setEmployee(employee);
        position.setPositionName("Senior Developer");
        position.setSalary(60000.0);
        position.setStartDate(LocalDate.of(2022, 1, 1));
        position.setEndDate(LocalDate.of(2024, 1, 1));
    }

    @Test
    public void addPositionToEmployee_ShouldAddPosition() {
        when(personRepository.findById(employee.getId())).thenReturn(Optional.of(employee));
        when(positionMapper.toEntity(any(PositionCommand.class))).thenReturn(position);
        when(positionRepository.existsOverlappingPosition(any(UUID.class), any(LocalDate.class), any(LocalDate.class))).thenReturn(false);
        when(positionRepository.save(any(Position.class))).thenReturn(position);

        positionService.addPositionToEmployee(employee.getId(), positionCommand);

        verify(personRepository).findById(employee.getId());
        verify(positionRepository).existsOverlappingPosition(any(UUID.class), any(LocalDate.class), any(LocalDate.class));
        verify(positionRepository).save(positionArgumentCaptor.capture());

        Position savedPosition = positionArgumentCaptor.getValue();
        assertThat(savedPosition).isNotNull();
        assertThat(savedPosition.getPositionName()).isEqualTo("Senior Developer");
        assertThat(savedPosition.getSalary()).isEqualTo(60000.0);
    }

    @Test
    public void addPositionToEmployee_ShouldThrowPersonNotFoundException() {
        when(personRepository.findById(employee.getId())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> positionService.addPositionToEmployee(employee.getId(), positionCommand))
                .isInstanceOf(PersonNotFoundException.class)
                .hasMessageContaining("Employee not found");

        verify(personRepository).findById(employee.getId());
    }

    @Test
    public void addPositionToEmployee_ShouldThrowInvalidPersonTypeException() {
        Person nonEmployee = new Student(); // Using a different subclass of Person
        nonEmployee.setId(employee.getId());

        when(personRepository.findById(employee.getId())).thenReturn(Optional.of(nonEmployee));

        assertThatThrownBy(() -> positionService.addPositionToEmployee(employee.getId(), positionCommand))
                .isInstanceOf(InvalidPersonTypeException.class)
                .hasMessageContaining("Person is not an employee");

        verify(personRepository).findById(employee.getId());
    }

    @Test
    public void addPositionToEmployee_ShouldThrowPositionOverlapException() {
        when(personRepository.findById(employee.getId())).thenReturn(Optional.of(employee));
        when(positionRepository.existsOverlappingPosition(any(UUID.class), any(LocalDate.class), any(LocalDate.class))).thenReturn(true);

        assertThatThrownBy(() -> positionService.addPositionToEmployee(employee.getId(), positionCommand))
                .isInstanceOf(PositionOverlapException.class)
                .hasMessageContaining("Position dates overlap with an existing position");

        verify(personRepository).findById(employee.getId());
        verify(positionRepository).existsOverlappingPosition(any(UUID.class), any(LocalDate.class), any(LocalDate.class));
    }

    @Test
    public void removePositionFromEmployee_ShouldRemovePosition() {
        when(personRepository.findById(employee.getId())).thenReturn(Optional.of(employee));
        when(positionRepository.existsById(position.getId())).thenReturn(true);

        positionService.removePositionFromEmployee(employee.getId(), position.getId());

        verify(personRepository).findById(employee.getId());
        verify(positionRepository).existsById(position.getId());
        verify(positionRepository).deleteByIdAndEmployeeId(position.getId(), employee.getId());
    }

    @Test
    public void removePositionFromEmployee_ShouldThrowPersonNotFoundException() {
        when(personRepository.findById(employee.getId())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> positionService.removePositionFromEmployee(employee.getId(), position.getId()))
                .isInstanceOf(PersonNotFoundException.class)
                .hasMessageContaining("Employee not found");

        verify(personRepository).findById(employee.getId());
    }

    @Test
    public void removePositionFromEmployee_ShouldThrowInvalidPersonTypeException() {
        Person nonEmployee = new Student();
        nonEmployee.setId(employee.getId());

        when(personRepository.findById(employee.getId())).thenReturn(Optional.of(nonEmployee));

        assertThatThrownBy(() -> positionService.removePositionFromEmployee(employee.getId(), position.getId()))
                .isInstanceOf(InvalidPersonTypeException.class)
                .hasMessageContaining("Person is not an employee");

        verify(personRepository).findById(employee.getId());
    }

    @Test
    public void removePositionFromEmployee_ShouldThrowPositionNotFoundException() {
        when(personRepository.findById(employee.getId())).thenReturn(Optional.of(employee));
        when(positionRepository.existsById(position.getId())).thenReturn(false);

        assertThatThrownBy(() -> positionService.removePositionFromEmployee(employee.getId(), position.getId()))
                .isInstanceOf(PositionNotFoundException.class)
                .hasMessageContaining("Position not found");

        verify(personRepository).findById(employee.getId());
        verify(positionRepository).existsById(position.getId());
    }
}
