package org.example.personmanagerapi.position.mapper;

import org.example.personmanagerapi.position.model.Position;
import org.example.personmanagerapi.position.model.PositionCommand;
import org.example.personmanagerapi.position.model.PositionDTO;
import org.springframework.stereotype.Component;

@Component
public class PositionMapper {
    public PositionDTO toDTO(Position position) {
        return PositionDTO.builder()
                .positionName(position.getPositionName())
                .salary(position.getSalary())
                .startDate(position.getStartDate())
                .endDate(position.getEndDate())
                .build();
    }

    public Position toEntity(PositionCommand dto) {
        Position position = new Position();
        position.setPositionName(dto.getPositionName());
        position.setSalary(dto.getSalary());
        position.setStartDate(dto.getStartDate());
        position.setEndDate(dto.getEndDate());
        return position;
    }

}
