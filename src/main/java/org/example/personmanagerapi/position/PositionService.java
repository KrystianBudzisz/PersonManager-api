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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.UUID;

@Service
public class PositionService {

    @Autowired
    private PositionRepository positionRepository;

    @Autowired
    private PersonRepository personRepository;

    @Autowired
    private PositionMapper positionMapper;

    @Transactional
    public void addPositionToEmployee(UUID employeeId, PositionCommand positionCommand) {
        Person person = personRepository.findById(employeeId)
                .orElseThrow(() -> new PersonNotFoundException("Employee not found"));

        if (!(person instanceof Employee employee)) {
            throw new InvalidPersonTypeException("Person is not an employee");
        }

        if (!isEndDateValid(positionCommand.getStartDate(), positionCommand.getEndDate())) {
            throw new IllegalArgumentException("End date must be after start date");
        }

        validatePositionDates(employee.getId(), positionCommand);

        Position position = positionMapper.toEntity(positionCommand);
        position.setEmployee(employee);
        employee.addPosition(position);

        employee.setCurrentPosition(position.getPositionName());
        employee.setCurrentSalary(position.getSalary());

        positionRepository.save(position);
        personRepository.save(employee);

        positionMapper.toDTO(position);
    }

    private boolean isEndDateValid(LocalDate startDate, LocalDate endDate) {
        return endDate.isAfter(startDate) || endDate.isEqual(startDate);
    }

    private void validatePositionDates(UUID employeeId, PositionCommand newPosition) {
        if (positionRepository.existsOverlappingPosition(employeeId, newPosition.getStartDate(), newPosition.getEndDate())) {
            throw new PositionOverlapException("Position dates overlap with an existing position");
        }
    }

    @Transactional
    public void removePositionFromEmployee(UUID employeeId, Long positionId) {
        Person person = personRepository.findById(employeeId)
                .orElseThrow(() -> new PersonNotFoundException("Employee not found"));

        if (!(person instanceof Employee)) {
            throw new InvalidPersonTypeException("Person is not an employee");
        }

        if (!positionRepository.existsById(positionId)) {
            throw new PositionNotFoundException("Position not found");
        }

        positionRepository.deleteByIdAndEmployeeId(positionId, employeeId);
    }
}
