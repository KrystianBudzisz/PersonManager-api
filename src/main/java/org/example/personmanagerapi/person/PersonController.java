package org.example.personmanagerapi.person;

import jakarta.validation.Valid;
import org.example.personmanagerapi.employee.EmployeeDTO;
import org.example.personmanagerapi.person.model.PersonCommand;
import org.example.personmanagerapi.person.model.PersonDTO;
import org.example.personmanagerapi.person.search.PersonSearchCriteria;
import org.example.personmanagerapi.position.PositionService;
import org.example.personmanagerapi.position.model.PositionCommand;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/persons")
public class PersonController {

    @Autowired
    private PersonService personService;

    @Autowired
    private PositionService positionService;


    @GetMapping("/search")
    @ResponseStatus(HttpStatus.OK)
    public Page<PersonDTO> searchPersons(PersonSearchCriteria criteria,
                                         @PageableDefault(page = 0, size = 10) Pageable pageable) {
        return personService.searchPersons(criteria, pageable);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('ADMIN')")
    public PersonDTO createPerson(@RequestBody @Valid PersonCommand personCommand) {
        return personService.createPerson(personCommand);
    }

    @PostMapping("/{employeeId}/position")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAnyRole('ADMIN', 'EMPLOYEE')")
    public EmployeeDTO addPositionToEmployee(@PathVariable UUID employeeId, @RequestBody @Valid PositionCommand positionCommand) {
        positionService.addPositionToEmployee(employeeId, positionCommand);
        return (EmployeeDTO) personService.getPersonById(employeeId);
    }

    @DeleteMapping("/{employeeId}/position/{positionId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasAnyRole('ADMIN', 'EMPLOYEE')")
    public void removePositionFromEmployee(@PathVariable UUID employeeId, @PathVariable Long positionId) {
        positionService.removePositionFromEmployee(employeeId, positionId);
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasRole('ADMIN')")
    public PersonDTO updatePerson(@PathVariable UUID id, @RequestBody @Valid PersonCommand personCommand) {
        return personService.updatePerson(id, personCommand);
    }

}
