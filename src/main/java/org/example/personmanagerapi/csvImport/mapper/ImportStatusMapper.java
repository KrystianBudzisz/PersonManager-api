package org.example.personmanagerapi.csvImport.mapper;

import org.example.personmanagerapi.csvImport.model.ImportStatus;
import org.example.personmanagerapi.csvImport.model.ImportStatusDTO;
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
