package org.example.personmanagerapi.position;

import org.example.personmanagerapi.position.model.Position;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;

public interface PositionRepository extends JpaRepository<Position, Long> {
    @Query("SELECT CASE WHEN COUNT(p) > 0 THEN TRUE ELSE FALSE END FROM Position p WHERE p.employee.id = :employeeId AND (:startDate BETWEEN p.startDate AND p.endDate OR :endDate BETWEEN p.startDate AND p.endDate OR p.startDate BETWEEN :startDate AND :endDate OR p.endDate BETWEEN :startDate AND :endDate)")
    boolean existsOverlappingPosition(@Param("employeeId") Long employeeId, @Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    void deleteByIdAndEmployeeId(Long positionId, Long employeeId);
}
