package br.com.ufrn.imd.Project_Manager.service;

import br.com.ufrn.imd.Project_Manager.dtos.api.PositionRequest;
import br.com.ufrn.imd.Project_Manager.dtos.api.PositionResponse;
import br.com.ufrn.imd.Project_Manager.model.Position;
import br.com.ufrn.imd.Project_Manager.repository.PositionRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class PositionService {

    @Autowired
    private PositionRepository positionRepository;

    public PositionResponse toPositionResponse(Position position) {
        return new PositionResponse(
                position.getId(),
                position.getName(),
                position.getLevel(),
                position.getDescription()
        );
    }

    public Page<PositionResponse> getPositions(String text, Pageable pageable) {
        Page<Position> positionsPage = positionRepository.searchPositions(text, pageable);
        return positionsPage.map(this::toPositionResponse);
    }

    public PositionResponse getPositionById(Long positionId) {
        Position position = positionRepository.findById(positionId)
                .orElseThrow(() -> new RuntimeException("Position not found!"));
        return toPositionResponse(position);
    }

    @Transactional
    public PositionResponse createPosition(PositionRequest positionRequest) {

        boolean exists = positionRepository.existsByNameAndLevel(
                positionRequest.name(),
                positionRequest.level()
        );

        if (exists) {
            throw new RuntimeException("Conflito: O cargo '" + positionRequest.name() +
                    "' com nível '" + positionRequest.level() + "' já existe.");
        }

        Position position = new Position(
                positionRequest.name(),
                positionRequest.level(),
                positionRequest.description());

        Position savedPosition = positionRepository.save(position);
        return toPositionResponse(savedPosition);
    }

    @Transactional
    public PositionResponse updatePosition(Long positionId, PositionRequest positionRequest) {
        Position position = positionRepository.findById(positionId)
                .orElseThrow(() -> new RuntimeException("Position not found!"));

        if (positionRequest.name() != null) {
            position.setName(positionRequest.name());
        }

        if (positionRequest.level() != null) {
            position.setLevel(positionRequest.level());
        }

        boolean exists = positionRepository.existsByNameAndLevel(position.getName(), position.getLevel());
        if (exists) {
            throw new RuntimeException("Conflito: O cargo '" + position.getName() + "' com nível '" + position.getLevel() + "' já existe.");
        }

        if (positionRequest.description() != null) {
            position.setDescription(positionRequest.description());
        }

        Position updatedPosition = positionRepository.save(position);
        return toPositionResponse(updatedPosition);
    }

    @Transactional
    public void deletePosition(Long positionId) {
        Position position = positionRepository.findById(positionId)
                .orElseThrow(() -> new RuntimeException("Position not found!"));
        positionRepository.delete(position);
    }

}
