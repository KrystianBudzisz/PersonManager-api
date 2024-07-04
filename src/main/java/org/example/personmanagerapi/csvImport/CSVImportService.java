package org.example.personmanagerapi.csvImport;

import org.example.personmanagerapi.csvImport.model.ImportStatus;
import org.example.personmanagerapi.csvImport.model.ImportStatusDTO;
import org.example.personmanagerapi.person.PersonService;
import org.example.personmanagerapi.person.model.Person;
import org.example.personmanagerapi.person.model.PersonCommand;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@Service
public class ImportService {

    private static final Logger logger = LoggerFactory.getLogger(ImportService.class);
    private final Lock lock = new ReentrantLock();

    @Autowired
    private PersonService personService;

    @Autowired
    private ImportStatusRepository importStatusRepository;

    @Transactional
    public void processCSV(MultipartFile file) {
        if (!lock.tryLock()) {
            logger.error("An import is already in progress.");
            throw new RuntimeException("An import is already in progress.");
        }

        ImportStatus importStatus = new ImportStatus();
        importStatus.setCreatedDate(LocalDateTime.now());
        importStatus.setStatus("IN_PROGRESS");
        importStatusRepository.save(importStatus);

        try {
            byte[] fileContent = file.getBytes();  // Read file content into a byte array
            CompletableFuture.runAsync(() -> processFileContent(fileContent, importStatus));
        } catch (Exception e) {
            importStatus.setCompletedDate(LocalDateTime.now());
            importStatus.setStatus("FAILED");
            importStatus.setErrorMessage(e.getMessage());
            importStatusRepository.save(importStatus);
            logger.error("CSV Processing failed: {}", e.getMessage(), e);
            throw new RuntimeException("CSV Processing failed: " + e.getMessage(), e);
        } finally {
            lock.unlock();
        }
    }

    private void processFileContent(byte[] fileContent, ImportStatus importStatus) {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(fileContent)))) {
            String line;
            int rowCount = 0;
            while ((line = reader.readLine()) != null) {
                // Assuming the CSV has a header and skipping it
                if (rowCount == 0) {
                    rowCount++;
                    continue;
                }

                String[] columns = line.split(",");
                PersonCommand personCommand = mapToPersonCommand(columns);
                logger.debug("Processing row {}: {}", rowCount, personCommand);
                personService.createPerson(personCommand);
                rowCount++;
                importStatus.setProcessedRows(rowCount);
            }

            importStatus.setCompletedDate(LocalDateTime.now());
            importStatus.setStatus("COMPLETED");
            importStatusRepository.save(importStatus);

        } catch (Exception e) {
            importStatus.setCompletedDate(LocalDateTime.now());
            importStatus.setStatus("FAILED");
            importStatus.setErrorMessage(e.getMessage());
            importStatusRepository.save(importStatus);
            logger.error("CSV Processing failed: {}", e.getMessage(), e);
            throw new RuntimeException("CSV Processing failed: " + e.getMessage(), e);
        }
    }

    private PersonCommand mapToPersonCommand(String[] columns) {
        PersonCommand personCommand = new PersonCommand();
        personCommand.setType(columns[0]);
        personCommand.setFirstName(columns[1]);
        personCommand.setLastName(columns[2]);
        personCommand.setPesel(columns[3]);
        personCommand.setHeight(Double.parseDouble(columns[4]));
        personCommand.setWeight(Double.parseDouble(columns[5]));
        personCommand.setEmail(columns[6]);

        Map<String, Object> typeSpecificFields = new HashMap<>();
        if (columns[0].equalsIgnoreCase("Student")) {
            typeSpecificFields.put("universityName", columns[7]);
            typeSpecificFields.put("yearOfStudy", Integer.parseInt(columns[8]));
            typeSpecificFields.put("fieldOfStudy", columns[9]);
            typeSpecificFields.put("scholarship", Double.parseDouble(columns[10]));
        } else if (columns[0].equalsIgnoreCase("Retiree")) {
            typeSpecificFields.put("pension", Double.parseDouble(columns[7]));
            typeSpecificFields.put("yearsWorked", Integer.parseInt(columns[8]));
        } else if (columns[0].equalsIgnoreCase("Employee")) {
            typeSpecificFields.put("employmentDate", columns[7]);
            typeSpecificFields.put("currentPosition", columns[8]);
            typeSpecificFields.put("currentSalary", Double.parseDouble(columns[9]));
        }

        personCommand.setTypeSpecificFields(typeSpecificFields);
        return personCommand;
    }
}


