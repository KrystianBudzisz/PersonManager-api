package org.example.personmanagerapi.csvImport;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ImportStatusUpdateService {

    private final JdbcTemplate jdbcTemplate;

    public ImportStatusUpdateService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void setImportStatusFailed(Long importStatusId) {
        String sql = "UPDATE import_status SET status = 'FAILED' WHERE id = ?";
        jdbcTemplate.update(sql, importStatusId);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void updateImportStatusCompleted(Long importStatusId, int processedRecords) {
        String sql = "UPDATE import_status SET status = 'COMPLETED', processed_records = ? WHERE id = ?";
        jdbcTemplate.update(sql, processedRecords, importStatusId);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void updateProcessedRecords(Long importStatusId, int processedRecords) {
        String sql = "UPDATE import_status SET processed_records = ? WHERE id = ?";
        jdbcTemplate.update(sql, processedRecords, importStatusId);
    }

}









