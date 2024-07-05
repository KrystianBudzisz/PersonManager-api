package org.example.personmanagerapi.strategy;


import org.example.personmanagerapi.person.model.Person;
import org.example.personmanagerapi.person.model.PersonCommand;
import org.example.personmanagerapi.rentiree.model.Retiree;
import org.example.personmanagerapi.rentiree.model.RetireeCommand;
import org.example.personmanagerapi.utils.CommandUtils;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Component
public class RetireeStrategy implements PersonTypeStrategy {

    @Override
    public Retiree preparePerson(PersonCommand personCommand) {
        RetireeCommand retireeCommand = CommandUtils.convertToSpecificCommand(personCommand.getTypeSpecificFields(), RetireeCommand.class);

        Retiree retiree = new Retiree();
        retiree.setFirstName(personCommand.getFirstName());
        retiree.setLastName(personCommand.getLastName());
        retiree.setPesel(personCommand.getPesel());
        retiree.setHeight(personCommand.getHeight());
        retiree.setWeight(personCommand.getWeight());
        retiree.setEmail(personCommand.getEmail());
        retiree.setPension(retireeCommand.getPension());
        retiree.setYearsWorked(retireeCommand.getYearsWorked());
        return retiree;
    }

    @Override
    public List<String> getRequiredFields() {
        return Arrays.asList("pension", "yearsWorked");
    }

    @Override
    public Map<String, Object> getDynamicCriteria() {
        Map<String, Object> criteria = new HashMap<>();
        criteria.put("pensionFrom", BigDecimal.class);
        criteria.put("pensionTo", BigDecimal.class);
        criteria.put("yearsWorkedFrom", Integer.class);
        criteria.put("yearsWorkedTo", Integer.class);
        return criteria;
    }

    @Override
    public Class<? extends Person> getPersonType() {
        return Retiree.class;
    }
}



