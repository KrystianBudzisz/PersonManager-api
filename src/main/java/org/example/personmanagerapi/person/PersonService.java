package org.example.personmanagerapi.person;


import org.example.personmanagerapi.exception.PersonNotFoundException;
import org.example.personmanagerapi.person.model.Person;
import org.example.personmanagerapi.person.model.PersonCommand;
import org.example.personmanagerapi.person.model.PersonDTO;
import org.example.personmanagerapi.person.model.PersonMapper;
import org.example.personmanagerapi.person.search.DynamicPersonSpecification;
import org.example.personmanagerapi.person.search.model.PersonSearchCriteria;
import org.example.personmanagerapi.strategy.PersonTypeStrategyFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class PersonService {

    @Autowired
    private PersonRepository personRepository;

    @Autowired
    private PersonMapper personMapper;

    @Autowired
    private PersonTypeStrategyFactory strategyFactory;

    public Page<PersonDTO> searchPersons(PersonSearchCriteria criteria, Pageable pageable) {
        Specification<Person> specification = new DynamicPersonSpecification(criteria);
        Page<Person> persons = personRepository.findAll(specification, pageable);
        return persons.map(personMapper::toDTO);
    }

    @Transactional(readOnly = true)
    public List<PersonDTO> getAllPersons() {
        List<Person> persons = personRepository.findAll();
        return persons.stream()
                .map(personMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public PersonDTO getPersonById(UUID personId) {
        Person person = personRepository.findById(personId)
                .orElseThrow(() -> new PersonNotFoundException("Person not found"));
        return personMapper.toDTO(person);
    }

    @Transactional
    public PersonDTO createPerson(PersonCommand personCommand) {
        Person person = strategyFactory.getStrategy(personCommand.getType())
                .preparePerson(personCommand);
        person = personRepository.save(person);
        return personMapper.toDTO(person);
    }

    @Transactional
    public PersonDTO updatePerson(UUID id, PersonCommand personCommand) {
        Person existingPerson = personRepository.findById(id)
                .orElseThrow(() -> new PersonNotFoundException("Person not found"));

        Person updatedPerson = strategyFactory.getStrategy(personCommand.getType())
                .preparePerson(personCommand);
        updatedPerson.setId(id); // Ensure the ID is set to the existing person's ID
        updatedPerson.setVersion(existingPerson.getVersion()); // Set the version for optimistic locking

        if (!existingPerson.getVersion().equals(updatedPerson.getVersion())) {
            throw new ObjectOptimisticLockingFailureException(Person.class, id);
        }

        Person savedPerson = personRepository.save(updatedPerson);
        return personMapper.toDTO(savedPerson);
    }
}