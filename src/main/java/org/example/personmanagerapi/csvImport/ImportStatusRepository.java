package org.example.personmanagerapi.csvImport;

import jakarta.persistence.LockModeType;
import org.example.personmanagerapi.csvImport.model.ImportStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ImportStatusRepository extends JpaRepository<ImportStatus, Long> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT s FROM ImportStatus s WHERE s.status = :status")
    Optional<ImportStatus> findFirstByStatusForUpdate(@Param("status") String status);
}






