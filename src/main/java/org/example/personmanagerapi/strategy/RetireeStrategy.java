package org.example.personmanagerapi.strategy;


import org.example.personmanagerapi.person.model.Person;
import org.example.personmanagerapi.person.model.PersonCommand;
import org.example.personmanagerapi.rentiree.model.Retiree;
import org.example.personmanagerapi.rentiree.model.RetireeCommand;
import org.example.personmanagerapi.utils.CommandUtils;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.Map;


@Component
public class RetireeStrategy implements PersonTypeStrategy {

    @Override
    public String getType() {
        return "retiree";
    }

    @Override
    public Class<? extends Person> getPersonClass() {
        return Retiree.class;
    }

    @Override
    public void insertSpecificFields(Map<String, Object> specificFields, JdbcTemplate jdbcTemplate, String pesel) {
        String retireeSql = "INSERT INTO retiree (id, pension, years_worked) " +
                "SELECT id, ?, ? FROM person WHERE pesel = ?";
        jdbcTemplate.update(retireeSql, Double.parseDouble(specificFields.get("field1").toString()), Integer.parseInt(specificFields.get("field2").toString()), pesel);
    }


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

}




