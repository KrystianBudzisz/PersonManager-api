package org.example.personmanagerapi.position;

import org.example.personmanagerapi.position.model.Position;


public class PositionMapper {
    public PositionDTO toDTO(Position position) {
        return PositionDTO.builder()
                .positionName(position.getPositionName())
                .salary(position.getSalary())
                .startDate(position.getStartDate())
                .endDate(position.getEndDate())
                .build();
    }

    public Position toEntity(PositionDTO dto) {
        Position position = new Position();
        position.setPositionName(dto.getPositionName());
        position.setSalary(dto.getSalary());
        position.setStartDate(dto.getStartDate());
        position.setEndDate(dto.getEndDate());
        return position;
    }
}
