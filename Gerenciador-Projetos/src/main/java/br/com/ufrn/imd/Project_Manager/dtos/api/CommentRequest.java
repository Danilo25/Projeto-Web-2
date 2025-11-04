package br.com.ufrn.imd.Project_Manager.dtos.api;

import java.time.LocalDateTime;

public record CommentRequest(String text, LocalDateTime createdAt, Long userId, Long taskId) {
}
