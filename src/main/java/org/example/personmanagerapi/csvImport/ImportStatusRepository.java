package org.example.personmanagerapi.csvImport;

import org.example.personmanagerapi.csvImport.model.ImportStatus;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ImportStatusRepository extends JpaRepository<ImportStatus, Long> {
}

