package br.com.ufrn.imd.Project_Manager.dtos;

public record UserRequest(
        String name,
        String email,
        String password,
        String position
) {}
