package br.com.ufrn.imd.Project_Manager.dtos;

import java.time.LocalDate;

public record ProjectRequest(String name, String description, LocalDate initialDate, LocalDate finalDate,
                            String status, Long teamId) {
}
