package org.example.personmanagerapi.person.search;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.example.personmanagerapi.employee.model.Employee;
import org.example.personmanagerapi.person.model.Person;
import org.example.personmanagerapi.student.model.Student;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class PersonSpecification implements Specification<Person> {

    private final Map<String, String> criteria;

    public PersonSpecification(Map<String, String> criteria) {
        this.criteria = criteria;
    }

    @Override
    public Predicate toPredicate(Root<Person> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
        List<Predicate> predicates = new ArrayList<>();

        criteria.forEach((key, value) -> {
            switch (key) {
                case "type":
                    predicates.add(cb.equal(root.type(), value));
                    break;
                case "firstName":
                    predicates.add(cb.like(cb.lower(root.get("firstName")), "%" + value.toLowerCase() + "%"));
                    break;
                case "lastName":
                    predicates.add(cb.like(cb.lower(root.get("lastName")), "%" + value.toLowerCase() + "%"));
                    break;
                case "pesel":
                    predicates.add(cb.like(cb.lower(root.get("pesel")), "%" + value.toLowerCase() + "%"));
                    break;
                case "email":
                    predicates.add(cb.like(cb.lower(root.get("email")), "%" + value.toLowerCase() + "%"));
                    break;
                case "heightFrom":
                    predicates.add(cb.greaterThanOrEqualTo(root.get("height"), Integer.valueOf(value)));
                    break;
                case "heightTo":
                    predicates.add(cb.lessThanOrEqualTo(root.get("height"), Integer.valueOf(value)));
                    break;
                case "weightFrom":
                    predicates.add(cb.greaterThanOrEqualTo(root.get("weight"), Integer.valueOf(value)));
                    break;
                case "weightTo":
                    predicates.add(cb.lessThanOrEqualTo(root.get("weight"), Integer.valueOf(value)));
                    break;
                // Add cases for specific fields like employee current salary, student university name, etc.
                case "currentSalaryFrom":
                    if (root.getJavaType().equals(Employee.class)) {
                        predicates.add(cb.greaterThanOrEqualTo(root.get("currentSalary"), Double.valueOf(value)));
                    }
                    break;
                case "currentSalaryTo":
                    if (root.getJavaType().equals(Employee.class)) {
                        predicates.add(cb.lessThanOrEqualTo(root.get("currentSalary"), Double.valueOf(value)));
                    }
                    break;
                case "universityName":
                    if (root.getJavaType().equals(Student.class)) {
                        predicates.add(cb.like(cb.lower(root.get("universityName")), "%" + value.toLowerCase() + "%"));
                    }
                    break;
                // Add more cases as needed
            }
        });

        return cb.and(predicates.toArray(new Predicate[0]));
    }
}

