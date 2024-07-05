package org.example.personmanagerapi.person.search;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.example.personmanagerapi.person.model.Person;
import org.example.personmanagerapi.strategy.PersonTypeStrategy;
import org.example.personmanagerapi.strategy.PersonTypeStrategyFactory;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class PersonSpecification implements Specification<Person> {

    private final PersonSearchCriteria criteria;
    private final Map<String, Object> dynamicCriteria;
    private final PersonTypeStrategyFactory strategyFactory;

    public PersonSpecification(PersonSearchCriteria criteria, Map<String, Object> dynamicCriteria, PersonTypeStrategyFactory strategyFactory) {
        this.criteria = criteria;
        this.dynamicCriteria = dynamicCriteria;
        this.strategyFactory = strategyFactory;
    }

    @Override
    public Predicate toPredicate(Root<Person> root, CriteriaQuery<?> query, CriteriaBuilder builder) {
        List<Predicate> predicates = new ArrayList<>();
        Map<String, Object> criteriaMap = criteria.toMap();

        if (criteriaMap.containsKey("type")) {
            String type = (String) criteriaMap.get("type");
            PersonTypeStrategy strategy = strategyFactory.getStrategy(type);
            if (strategy != null) {
                predicates.add(builder.equal(root.type(), strategy.getPersonType()));
            }
        }

        criteriaMap.forEach((key, value) -> {
            if (value != null) {
                switch (key) {
                    case "firstName":
                        predicates.add(builder.like(builder.lower(root.get("firstName")), "%" + value.toString().toLowerCase() + "%"));
                        break;
                    case "lastName":
                        predicates.add(builder.like(builder.lower(root.get("lastName")), "%" + value.toString().toLowerCase() + "%"));
                        break;
                    case "pesel":
                        predicates.add(builder.equal(root.get("pesel"), value));
                        break;
                    case "email":
                        predicates.add(builder.like(builder.lower(root.get("email")), "%" + value.toString().toLowerCase() + "%"));
                        break;
                    case "heightFrom":
                        predicates.add(builder.greaterThanOrEqualTo(root.get("height"), (Double) value));
                        break;
                    case "heightTo":
                        predicates.add(builder.lessThanOrEqualTo(root.get("height"), (Double) value));
                        break;
                    case "weightFrom":
                        predicates.add(builder.greaterThanOrEqualTo(root.get("weight"), (Double) value));
                        break;
                    case "weightTo":
                        predicates.add(builder.lessThanOrEqualTo(root.get("weight"), (Double) value));
                        break;
                }
            }
        });

        dynamicCriteria.forEach((key, value) -> {
            if (criteriaMap.containsKey(key)) {
                Object fieldValue = criteriaMap.get(key);
                if (fieldValue != null) {
                    if (fieldValue instanceof Range<?>) {
                        Range<?> range = (Range<?>) fieldValue;
                        if (range.getFrom() instanceof Comparable && range.getTo() instanceof Comparable) {
                            predicates.add(builder.between(root.get(key), (Comparable) range.getFrom(), (Comparable) range.getTo()));
                        }
                    } else {
                        predicates.add(builder.equal(root.get(key), fieldValue));
                    }
                }
            }
        });

        return builder.and(predicates.toArray(new Predicate[0]));
    }
}


