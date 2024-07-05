package org.example.personmanagerapi.csvImport;

import org.example.personmanagerapi.csvImport.model.ImportStatus;
import org.example.personmanagerapi.csvImport.model.ImportStatusDTO;
import org.example.personmanagerapi.csvImport.model.ImportStatusMapper;
import org.example.personmanagerapi.exception.CSVProcessingException;
import org.example.personmanagerapi.exception.ConcurrentImportException;
import org.example.personmanagerapi.exception.ImportStatusNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.concurrent.locks.ReentrantLock;

@Service
public class CSVImportService {

    private final ReentrantLock importLock = new ReentrantLock();

    @Autowired
    private CSVProcessAsync csvProcessAsync;
    @Autowired
    private ImportStatusRepository importStatusRepository;
    @Autowired
    private ImportStatusMapper importStatusMapper;

    public ImportStatusDTO startCSVImport(MultipartFile file) {
        if (file.isEmpty()) {
            throw new CSVProcessingException("Bad request. File is empty.");
        }

        if (!importLock.tryLock()) {
            throw new ConcurrentImportException("Another import is already in progress. Please wait until it completes.");
        }

        importLock.lock();

        try {
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
        } finally {
            importLock.unlock();
        }
    }


    public ImportStatusDTO getImportStatus(Long id) {
        ImportStatus importStatus = importStatusRepository.findById(id)
                .orElseThrow(() -> new ImportStatusNotFoundException("Import status not found"));
        return importStatusMapper.toDTO(importStatus);
    }
}