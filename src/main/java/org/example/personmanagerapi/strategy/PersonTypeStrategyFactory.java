package org.example.personmanagerapi.strategy;

import org.example.personmanagerapi.person.model.Person;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class PersonTypeStrategyFactory {

    private final Map<String, PersonTypeStrategy> strategies = new HashMap<>();

    @Autowired
    public PersonTypeStrategyFactory(Map<String, PersonTypeStrategy> strategyBeans) {
        strategyBeans.forEach((key, value) -> strategies.put(value.getClass().getSimpleName().replace("Strategy", "").toLowerCase(), value));
    }

    public PersonTypeStrategy getStrategy(String type) {
        return strategies.get(type.toLowerCase());
    }

    public Class<? extends Person> getPersonClass(String type) {
        PersonTypeStrategy strategy = getStrategy(type);
        return strategy != null ? strategy.getPersonClass() : null;
    }
}

