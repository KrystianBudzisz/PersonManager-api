package org.example.personmanagerapi.csvImport.model;

import org.springframework.stereotype.Component;

@Component
public class ImportStatusMapper {

    public ImportStatusDTO toDTO(ImportStatus importStatus) {
        return new ImportStatusDTO(
                importStatus.getId(),
                importStatus.getStatus(),
                importStatus.getCreatedDate(),
                importStatus.getStartedDate(),
                importStatus.getProcessedRecords()
        );
    }
}
