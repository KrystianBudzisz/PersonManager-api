package org.example.personmanagerapi.strategy;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class PersonTypeStrategyFactory {

    private final Map<String, PersonTypeStrategy> strategies = new HashMap<>();

    @Autowired
    public PersonTypeStrategyFactory(List<PersonTypeStrategy> strategyBeans) {
        for (PersonTypeStrategy strategy : strategyBeans) {
            strategies.put(strategy.getClass().getSimpleName().replace("Strategy", "").toLowerCase(), strategy);
        }
    }

    public PersonTypeStrategy getStrategy(String type) {
        return strategies.get(type.toLowerCase());
    }

    public Map<String, Object> getDynamicCriteria(String type) {
        PersonTypeStrategy strategy = getStrategy(type);
        return strategy != null ? strategy.getDynamicCriteria() : Collections.emptyMap();
    }
}
