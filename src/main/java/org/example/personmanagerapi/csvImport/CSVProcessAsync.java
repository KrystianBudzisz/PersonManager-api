package org.example.personmanagerapi.csvImport;

import com.opencsv.CSVReader;
import org.example.personmanagerapi.exception.CSVParsingException;
import org.example.personmanagerapi.exception.CSVProcessingException;
import org.example.personmanagerapi.exception.InvalidCSVFormatException;
import org.example.personmanagerapi.person.model.PersonCommand;
import org.example.personmanagerapi.strategy.PersonTypeStrategy;
import org.example.personmanagerapi.strategy.PersonTypeStrategyFactory;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class CSVProcessAsync {

    private static final int BATCH_SIZE = 10000;
    private final JdbcTemplate jdbcTemplate;
    private final ImportStatusUpdateService importStatusUpdateService;
    private final PersonTypeStrategyFactory personTypeStrategyFactory;

    public CSVProcessAsync(JdbcTemplate jdbcTemplate, ImportStatusUpdateService importStatusUpdateService, PersonTypeStrategyFactory personTypeStrategyFactory) {
        this.jdbcTemplate = jdbcTemplate;
        this.importStatusUpdateService = importStatusUpdateService;
        this.personTypeStrategyFactory = personTypeStrategyFactory;
    }


    @Async
    public void processCSVFile(MultipartFile file, Long importStatusId) {
        try (InputStream inputStream = file.getInputStream(); CSVReader reader = new CSVReader(new InputStreamReader(inputStream))) {

            String[] line;
            reader.readNext();
            int processedRecords = 0;
            List<PersonCommand> batch = new ArrayList<>();

            while ((line = reader.readNext()) != null) {
                processedRecords++;
                PersonCommand personCommand = preparePerson(line);
                batch.add(personCommand);

                if (batch.size() >= BATCH_SIZE) {
                    insertBatch(batch);
                    batch.clear();
                    importStatusUpdateService.updateProcessedRecords(importStatusId, processedRecords);
                }
            }

            if (!batch.isEmpty()) {
                insertBatch(batch);
                importStatusUpdateService.updateProcessedRecords(importStatusId, processedRecords);
            }

            importStatusUpdateService.updateImportStatusCompleted(importStatusId, processedRecords);
        } catch (IOException e) {
            importStatusUpdateService.setImportStatusFailed(importStatusId);
            throw new CSVParsingException("Failed to read CSV file", e);
        } catch (InvalidCSVFormatException | CSVParsingException e) {
            importStatusUpdateService.setImportStatusFailed(importStatusId);
            throw e;
        } catch (Exception e) {
            importStatusUpdateService.setImportStatusFailed(importStatusId);
            throw new CSVProcessingException("Unexpected error occurred while processing CSV file", e);
        }
    }

    private PersonCommand preparePerson(String[] fields) {
        if (fields.length < 7) {
            throw new InvalidCSVFormatException("Invalid CSV format: expected at least 7 fields, found " + fields.length);
        }

        try {
            PersonCommand command = new PersonCommand();
            command.setType(fields[0].trim());
            command.setFirstName(fields[1].trim());
            command.setLastName(fields[2].trim());
            command.setPesel(fields[3].trim());
            command.setHeight(Double.parseDouble(fields[4].trim()));
            command.setWeight(Double.parseDouble(fields[5].trim()));
            command.setEmail(fields[6].trim());

            Map<String, Object> specificFields = new HashMap<>();
            for (int i = 7; i < fields.length; i++) {
                specificFields.put("field" + (i - 6), fields[i].trim());
            }
            command.setTypeSpecificFields(specificFields);
            return command;
        } catch (NumberFormatException e) {
            throw new CSVParsingException("Error parsing numeric field in CSV", e);
        }
    }

    private void insertBatch(List<PersonCommand> batch) {
        String personSql = "INSERT INTO person (type, first_name, last_name, pesel, height, weight, email) " + "VALUES (?, ?, ?, ?, ?, ?, ?)";

        jdbcTemplate.batchUpdate(personSql, new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                PersonCommand command = batch.get(i);
                ps.setString(1, command.getType());
                ps.setString(2, command.getFirstName());
                ps.setString(3, command.getLastName());
                ps.setString(4, command.getPesel());
                ps.setDouble(5, command.getHeight());
                ps.setDouble(6, command.getWeight());
                ps.setString(7, command.getEmail());
            }

            @Override
            public int getBatchSize() {
                return batch.size();
            }
        });

        for (PersonCommand command : batch) {
            PersonTypeStrategy strategy = personTypeStrategyFactory.getStrategy(command.getType());
            strategy.insertSpecificFields(command.getTypeSpecificFields(), jdbcTemplate, command.getPesel());
        }

    }
}




