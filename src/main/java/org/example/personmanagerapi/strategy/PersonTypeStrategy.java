package org.example.personmanagerapi.strategy;


import org.example.personmanagerapi.person.model.Person;
import org.example.personmanagerapi.person.model.PersonCommand;

import java.util.List;
import java.util.Map;

public interface PersonTypeStrategy {

    Person preparePerson(PersonCommand personCommand);

    List<String> getRequiredFields();

    Map<String, Object> getDynamicCriteria();

    Class<? extends Person> getPersonType();
}



