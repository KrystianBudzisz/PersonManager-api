package org.example.personmanagerapi.csvImport;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.example.personmanagerapi.csvImport.model.ImportStatus;
import org.example.personmanagerapi.exception.CSVProcessingException;
import org.example.personmanagerapi.exception.ImportStatusNotFoundException;
import org.example.personmanagerapi.person.PersonService;
import org.example.personmanagerapi.person.model.PersonCommand;
import org.example.personmanagerapi.strategy.PersonTypeStrategy;
import org.example.personmanagerapi.strategy.PersonTypeStrategyFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class CSVProcessAsync {

    @Autowired
    private PersonTypeStrategyFactory personTypeStrategyFactory;

    @Autowired
    private ImportStatusRepository importStatusRepository;

    @Autowired
    private ImportStatusUpdateService importStatusUpdateService;

    @Autowired
    private PersonService personService;

    @Async
    @Transactional
    public void processCSVFile(MultipartFile file, Long importStatusId) {
        ImportStatus importStatus = importStatusRepository.findById(importStatusId)
                .orElseThrow(() -> new ImportStatusNotFoundException("Import status not found"));

        importStatus.setStartedDate(LocalDateTime.now());
        importStatus.setStatus("IN_PROGRESS");
        importStatusRepository.save(importStatus);

        int processedRecords = 0;

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream()));
             CSVParser csvParser = new CSVParser(reader, CSVFormat.DEFAULT.withFirstRecordAsHeader())) {

            for (CSVRecord record : csvParser) {
                try {
                    PersonCommand personCommand = processRecord(record);
                    personService.createPerson(personCommand);
                    processedRecords++;
                    importStatusUpdateService.updateProcessedRecords(importStatusId, processedRecords);
                } catch (Exception e) {
                    importStatusUpdateService.setImportStatusFailed(importStatusId);
                    throw new CSVProcessingException("Failed to process data", e);
                }
            }

            importStatus.setStatus("COMPLETED");
            importStatus.setProcessedRecords(processedRecords);
            importStatusRepository.save(importStatus);
        } catch (Exception e) {
            importStatusUpdateService.setImportStatusFailed(importStatusId);
            throw new CSVProcessingException("Failed to process data", e);
        }
    }

    public PersonCommand processRecord(CSVRecord record) {
        String type = record.get(0).trim();
        PersonTypeStrategy strategy = personTypeStrategyFactory.getStrategy(type);

        if (strategy == null) {
            throw new IllegalArgumentException("Unknown person type: " + type);
        }

        List<String> requiredFields = strategy.getRequiredFields();
        int totalFields = 7 + requiredFields.size();

        if (record.size() < totalFields) {
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



