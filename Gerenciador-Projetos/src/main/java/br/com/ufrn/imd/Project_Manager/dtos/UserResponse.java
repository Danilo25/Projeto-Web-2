package br.com.ufrn.imd.Project_Manager.dtos;

public record UserResponse(
        Long id,
        String name,
        String email,
        String position
) {}
