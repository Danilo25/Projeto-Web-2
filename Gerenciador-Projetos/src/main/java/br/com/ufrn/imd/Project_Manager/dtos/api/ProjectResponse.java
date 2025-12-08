package br.com.ufrn.imd.Project_Manager.dtos.api;

import java.time.LocalDate;
import java.util.List;

public record ProjectResponse(Long id, String name, String description, LocalDate initialDate, LocalDate finalDate,
                              String status, Long teamId, List<Long> frameIds, Long clientId) {
}
