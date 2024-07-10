package org.example.personmanagerapi.person.search;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.example.personmanagerapi.person.model.Person;
import org.example.personmanagerapi.strategy.PersonTypeStrategyFactory;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class PersonSpecification implements Specification<Person> {

    private final PersonSearchCriteria criteria;
    private final PersonTypeStrategyFactory strategyFactory;

    public PersonSpecification(PersonSearchCriteria criteria, PersonTypeStrategyFactory strategyFactory) {
        this.criteria = criteria;
        this.strategyFactory = strategyFactory;
    }

    @Override
    public Predicate toPredicate(Root<Person> root, CriteriaQuery<?> query, CriteriaBuilder builder) {
        List<Predicate> predicates = new ArrayList<>();

        if (criteria.getType() != null) {
            Class<? extends Person> personClass = strategyFactory.getPersonClass(criteria.getType());
            if (personClass != null) {
                predicates.add(builder.equal(root.type(), personClass));
            }
        }

        if (criteria.getFirstName() != null) {
            predicates.add(builder.like(builder.lower(root.get("firstName")), "%" + criteria.getFirstName().toLowerCase() + "%"));
        }

        if (criteria.getLastName() != null) {
            predicates.add(builder.like(builder.lower(root.get("lastName")), "%" + criteria.getLastName().toLowerCase() + "%"));
        }

        if (criteria.getPesel() != null) {
            predicates.add(builder.equal(root.get("pesel"), criteria.getPesel()));
        }

        if (criteria.getEmail() != null) {
            predicates.add(builder.like(builder.lower(root.get("email")), "%" + criteria.getEmail().toLowerCase() + "%"));
        }

        if (criteria.getHeightFrom() != null) {
            predicates.add(builder.greaterThanOrEqualTo(root.get("height"), criteria.getHeightFrom()));
        }

        if (criteria.getHeightTo() != null) {
            predicates.add(builder.lessThanOrEqualTo(root.get("height"), criteria.getHeightTo()));
        }

        if (criteria.getWeightFrom() != null) {
            predicates.add(builder.greaterThanOrEqualTo(root.get("weight"), criteria.getWeightFrom()));
        }

        if (criteria.getWeightTo() != null) {
            predicates.add(builder.lessThanOrEqualTo(root.get("weight"), criteria.getWeightTo()));
        }

        if (criteria.getCurrentSalaryFrom() != null) {
            predicates.add(builder.greaterThanOrEqualTo(root.get("currentSalary"), criteria.getCurrentSalaryFrom()));
        }

        if (criteria.getCurrentSalaryTo() != null) {
            predicates.add(builder.lessThanOrEqualTo(root.get("currentSalary"), criteria.getCurrentSalaryTo()));
        }

        criteria.getDynamicCriteria().forEach((key, value) -> {
            if (value != null) {
                predicates.add(builder.equal(root.get(key), value));
            }
        });

        return builder.and(predicates.toArray(new Predicate[0]));
    }
}





