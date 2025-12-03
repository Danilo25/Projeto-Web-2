package br.com.ufrn.imd.Project_Manager.dtos.api;

public record UserRequest(
        String name,
        String email,
        String password,
        Long positionId
) {}
