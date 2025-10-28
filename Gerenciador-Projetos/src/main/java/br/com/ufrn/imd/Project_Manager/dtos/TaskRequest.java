package br.com.ufrn.imd.Project_Manager.dtos;

import java.time.LocalDate;
import java.util.List;

public record TaskRequest(String name, String description, LocalDate initialDate, LocalDate finalDate,
                          String status, Long frameId, Long assigneeId) {
}
