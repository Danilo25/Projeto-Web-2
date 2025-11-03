package br.com.ufrn.imd.Project_Manager.dtos.api;

public record AddressResponse(
        Long id,
        String publicPlace,
        String district,
        String city,
        String state,
        String zipCode,
        Long userId
) {}
