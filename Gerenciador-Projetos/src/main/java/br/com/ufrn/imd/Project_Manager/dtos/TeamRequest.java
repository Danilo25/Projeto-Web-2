package br.com.ufrn.imd.Project_Manager.dtos;

import java.util.List;

public record TeamRequest(
        String name,
        String description,
        List<Long> userIds
) {}
