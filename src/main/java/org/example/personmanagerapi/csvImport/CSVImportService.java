package org.example.personmanagerapi.csvImport;

import org.example.personmanagerapi.csvImport.mapper.ImportStatusMapper;
import org.example.personmanagerapi.csvImport.model.ImportStatus;
import org.example.personmanagerapi.csvImport.model.ImportStatusDTO;
import org.example.personmanagerapi.exception.CSVProcessingException;
import org.example.personmanagerapi.exception.ConcurrentImportException;
import org.example.personmanagerapi.exception.ImportStatusNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class CSVImportService {

    private final CSVProcessAsync csvProcessAsync;
    private final ImportStatusRepository importStatusRepository;
    private final ImportStatusMapper importStatusMapper;

    public CSVImportService(CSVProcessAsync csvProcessAsync, ImportStatusRepository importStatusRepository, ImportStatusMapper importStatusMapper) {
        this.csvProcessAsync = csvProcessAsync;
        this.importStatusRepository = importStatusRepository;
        this.importStatusMapper = importStatusMapper;
    }

    public ImportStatusDTO startCSVImport(MultipartFile file) {
        if (file.isEmpty()) {
            throw new CSVProcessingException("Bad request. File is empty.");
        }
        Optional<ImportStatus> activeImport = importStatusRepository.findFirstByStatus("IN_PROGRESS");
        if (activeImport.isPresent()) {
            throw new ConcurrentImportException("Another import is already in progress. Please wait until it completes.");
        }
        ImportStatus importStatus = new ImportStatus();
        importStatus.setStatus("IN_PROGRESS");
        importStatus.setCreatedDate(LocalDateTime.now());


        importStatus = importStatusRepository.save(importStatus);

        try {
            csvProcessAsync.processCSVFile(file, importStatus.getId());
            return importStatusMapper.toDTO(importStatus);
        } catch (Exception e) {
            importStatus.setStatus("FAILED");
            importStatusRepository.save(importStatus);
            throw new CSVProcessingException("CSV file processing has failed to start", e);
        }
    }

    @Transactional(readOnly = true)
    public ImportStatusDTO getImportStatus(Long id) {
        ImportStatus importStatus = importStatusRepository.findById(id)
                .orElseThrow(() -> new ImportStatusNotFoundException("Import status not found"));
        return importStatusMapper.toDTO(importStatus);
    }
}


