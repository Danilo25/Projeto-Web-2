package br.com.ufrn.imd.Project_Manager.dtos.api;

import java.util.List;

public record FrameResponse(Long id, String name, Integer orderIndex, Long projectId, List<TaskResponse> tasks) {
}
