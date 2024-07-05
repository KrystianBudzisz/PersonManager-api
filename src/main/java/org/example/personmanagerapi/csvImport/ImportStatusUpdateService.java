package org.example.personmanagerapi.csvImport;

import org.example.personmanagerapi.csvImport.model.ImportStatus;
import org.example.personmanagerapi.exception.ImportStatusNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ImportStatusUpdateService {

    @Autowired
    private ImportStatusRepository importStatusRepository;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void updateProcessedRecords(Long importStatusId, int processedRecords) {
        ImportStatus importStatus = importStatusRepository.findById(importStatusId)
                .orElseThrow(() -> new ImportStatusNotFoundException("Import status not found"));
        importStatus.setProcessedRecords(processedRecords);
        importStatusRepository.save(importStatus);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void setImportStatusFailed(Long importStatusId) {
        ImportStatus importStatus = importStatusRepository.findById(importStatusId)
                .orElseThrow(() -> new ImportStatusNotFoundException("Import status not found"));
        importStatus.setStatus("FAILED");
        importStatusRepository.save(importStatus);
    }
}
