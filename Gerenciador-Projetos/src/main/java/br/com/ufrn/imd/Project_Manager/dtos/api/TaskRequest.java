package br.com.ufrn.imd.Project_Manager.dtos.api;

import java.time.LocalDate;

public record TaskRequest(String name, String description, LocalDate initialDate, LocalDate finalDate,
                          String status, Long frameId, Long assigneeId) {
}
