package br.com.ufrn.imd.Project_Manager.dtos;

import java.time.LocalDate;

public record DashboardPageProjectResponse(Long id, String name, String teamName, LocalDate finalDate, String status) {
}
