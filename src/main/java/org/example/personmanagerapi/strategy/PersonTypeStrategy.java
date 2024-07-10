package org.example.personmanagerapi.strategy;


import org.example.personmanagerapi.person.model.Person;
import org.example.personmanagerapi.person.model.PersonCommand;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.Map;

public interface PersonTypeStrategy {
    String getType();

    void insertSpecificFields(Map<String, Object> specificFields, JdbcTemplate jdbcTemplate, String pesel);

    Person preparePerson(PersonCommand personCommand);

    Class<? extends Person> getPersonClass();

}




