package br.com.ufrn.imd.Project_Manager.dtos.api;

public record UserResponse(
        Long id,
        String name,
        String email,
        PositionResponse position
) {}
