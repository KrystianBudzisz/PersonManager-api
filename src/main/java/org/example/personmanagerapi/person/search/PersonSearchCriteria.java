package org.example.personmanagerapi.person.search;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PersonSearchCriteria {
    private String type;
    private String firstName;
    private String lastName;
    private Integer ageFrom;
    private Integer ageTo;
    private String pesel;
    private Double heightFrom;
    private Double heightTo;
    private Double weightFrom;
    private Double weightTo;
    private String email;

    private Map<String, Object> dynamicCriteria = new HashMap<>();

    public void addDynamicCriteria(String key, Object value) {
        this.dynamicCriteria.put(key, value);
    }

    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        if (type != null) map.put("type", type);
        if (firstName != null) map.put("firstName", firstName);
        if (lastName != null) map.put("lastName", lastName);
        if (ageFrom != null) map.put("ageFrom", ageFrom);
        if (ageTo != null) map.put("ageTo", ageTo);
        if (pesel != null) map.put("pesel", pesel);
        if (heightFrom != null) map.put("heightFrom", heightFrom);
        if (heightTo != null) map.put("heightTo", heightTo);
        if (weightFrom != null) map.put("weightFrom", weightFrom);
        if (weightTo != null) map.put("weightTo", weightTo);
        if (email != null) map.put("email", email);
        map.putAll(dynamicCriteria);
        return map;
    }
}

