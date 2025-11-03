package br.com.ufrn.imd.Project_Manager.dtos.api;

import java.util.Set;

public record TeamResponse(
        Long id,
        String name,
        String description,
        Set<Long> userIds
) {}
