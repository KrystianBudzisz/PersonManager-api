package org.example.personmanagerapi.csvImport;

import org.example.personmanagerapi.csvImport.model.ImportStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ImportStatusRepository extends JpaRepository<ImportStatus, Long> {
    Optional<ImportStatus> findFirstByStatus(String inProgress);
}

