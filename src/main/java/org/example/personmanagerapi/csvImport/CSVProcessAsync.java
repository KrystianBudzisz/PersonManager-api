package org.example.personmanagerapi.csvImport;

import jakarta.transaction.Transactional;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.example.personmanagerapi.csvImport.model.ImportStatus;
import org.example.personmanagerapi.person.PersonRepository;
import org.example.personmanagerapi.person.PersonService;
import org.example.personmanagerapi.person.model.PersonCommand;
import org.example.personmanagerapi.strategy.PersonTypeStrategy;
import org.example.personmanagerapi.strategy.PersonTypeStrategyFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.StreamSupport;

@Service
public class CSVProcessAsync {

    private static final Logger logger = LoggerFactory.getLogger(CSVProcessAsync.class);

    @Autowired
    private PersonService personService;

    @Autowired
    private PersonTypeStrategyFactory personTypeStrategyFactory;

    @Autowired
    private ImportStatusRepository importStatusRepository;

    @Async
    @Transactional
    public void saveCSVData(MultipartFile file, UUID importStatusId) {
        ImportStatus importStatus = importStatusRepository.findById(importStatusId)
                .orElseThrow(() -> new RuntimeException("Import status not found"));

        importStatus.setStartedDate(LocalDateTime.now());
        importStatus.setStatus("IN_PROGRESS");
        importStatusRepository.save(importStatus);

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream()));
             CSVParser csvParser = new CSVParser(reader, CSVFormat.DEFAULT.withFirstRecordAsHeader())) {

            int processedRecords = 0;

            for (CSVRecord record : csvParser) {
                try {
                    PersonCommand personCommand = processRecord(record);
                    personService.createPerson(personCommand);
                    processedRecords++;
                } catch (Exception e) {
                    logger.error("Error processing record: {}", record, e);
                    throw new RuntimeException("Failed to insert data", e);
                }
            }

            importStatus.setStatus("COMPLETED");
            importStatus.setProcessedRecords(processedRecords);
            importStatusRepository.save(importStatus);
            logger.info("CSV import completed successfully. Total records processed: {}", processedRecords);
        } catch (Exception e) {
            importStatus.setStatus("FAILED");
            importStatusRepository.save(importStatus);
            logger.error("Exception occurred during CSV import", e);
            throw new RuntimeException("Failed to insert data", e);
        }
    }

    private PersonCommand processRecord(CSVRecord record) {
        logger.info("Processing line with type: {}", record.get(0));
        String type = record.get(0).trim();
        PersonTypeStrategy strategy = personTypeStrategyFactory.getStrategy(type);

        if (strategy == null) {
            logger.error("Unknown person type: {}", type);
            throw new IllegalArgumentException("Unknown person type: " + type);
        }

        List<String> requiredFields = strategy.getRequiredFields();
        int totalFields = 7 + requiredFields.size(); // Base fields + type-specific fields

        if (record.size() < totalFields) {
            logger.error("Invalid CSV format for type: {}", type);
            throw new IllegalArgumentException("Invalid CSV format for type: " + type);
        }

        PersonCommand personCommand = new PersonCommand();
        personCommand.setType(type);
        personCommand.setFirstName(record.get(1).trim());
        personCommand.setLastName(record.get(2).trim());
        personCommand.setPesel(record.get(3).trim());
        personCommand.setHeight(Double.parseDouble(record.get(4).trim()));
        personCommand.setWeight(Double.parseDouble(record.get(5).trim()));
        personCommand.setEmail(record.get(6).trim());

        Map<String, Object> typeSpecificFields = new HashMap<>();
        for (int i = 0; i < requiredFields.size(); i++) {
            typeSpecificFields.put(requiredFields.get(i), record.get(7 + i).trim());
        }
        personCommand.setTypeSpecificFields(typeSpecificFields);

        return personCommand;
    }
}
