package org.example.personmanagerapi.person;


import org.example.personmanagerapi.exception.DuplicatePersonException;
import org.example.personmanagerapi.exception.InvalidPersonTypeException;
import org.example.personmanagerapi.exception.PersonNotFoundException;
import org.example.personmanagerapi.person.mapper.PersonMapper;
import org.example.personmanagerapi.person.model.Person;
import org.example.personmanagerapi.person.model.PersonCommand;
import org.example.personmanagerapi.person.model.PersonDTO;
import org.example.personmanagerapi.person.search.PersonSearchCriteria;
import org.example.personmanagerapi.person.search.PersonSpecification;
import org.example.personmanagerapi.strategy.PersonTypeStrategy;
import org.example.personmanagerapi.strategy.PersonTypeStrategyFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PersonService {

    private final PersonRepository personRepository;
    private final PersonMapper personMapper;
    private final PersonTypeStrategyFactory strategyFactory;

    public PersonService(PersonRepository personRepository, PersonMapper personMapper, PersonTypeStrategyFactory strategyFactory) {
        this.personRepository = personRepository;
        this.personMapper = personMapper;
        this.strategyFactory = strategyFactory;
    }

    @Transactional(readOnly = true)
    public PersonDTO getPersonById(Long personId) {
        Person person = personRepository.findById(personId)
                .orElseThrow(() -> new PersonNotFoundException("Person not found with ID: " + personId));
        return personMapper.toDTO(person);
    }

    @Transactional(readOnly = true)
    public Page<PersonDTO> searchPersons(PersonSearchCriteria criteria, Pageable pageable) {
        Specification<Person> specification = new PersonSpecification(criteria, strategyFactory);
        Page<Person> personPage = personRepository.findAll(specification, pageable);
        return personPage.map(personMapper::toDTO);
    }


    @Transactional
    public PersonDTO createPerson(PersonCommand personCommand) {
        PersonTypeStrategy strategy = strategyFactory.getStrategy(personCommand.getType());
        if (strategy == null) {
            throw new InvalidPersonTypeException("Person type not supported: " + personCommand.getType());
        }
        try {
            Person person = strategy.preparePerson(personCommand);
            person = personRepository.save(person);
            return personMapper.toDTO(person);
        } catch (DataIntegrityViolationException e) {
            throw new DuplicatePersonException("A person with the same unique identifier already exists.");
        }
    }

    @Transactional
    public PersonDTO updatePerson(Long id, PersonCommand personCommand) {
        Person existingPerson = personRepository.findById(id)
                .orElseThrow(() -> new PersonNotFoundException("Person not found with ID: " + id));

        PersonTypeStrategy strategy = strategyFactory.getStrategy(personCommand.getType());
        if (strategy == null) {
            throw new InvalidPersonTypeException("Person type not supported: " + personCommand.getType());
        }

        Person updatedPerson = strategy.preparePerson(personCommand);
        updatedPerson.setId(id);
        updatedPerson.setVersion(existingPerson.getVersion());

        if (!existingPerson.getVersion().equals(updatedPerson.getVersion())) {
            throw new ObjectOptimisticLockingFailureException(Person.class, id);
        }

        try {
            Person savedPerson = personRepository.save(updatedPerson);
            return personMapper.toDTO(savedPerson);
        } catch (DataIntegrityViolationException e) {
            throw new DuplicatePersonException("A person with the same unique identifier already exists.");
        }
    }
}



