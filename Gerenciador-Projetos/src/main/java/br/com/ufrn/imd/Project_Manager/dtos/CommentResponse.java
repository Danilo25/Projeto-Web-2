package br.com.ufrn.imd.Project_Manager.dtos;

import java.time.LocalDateTime;

public record CommentResponse(Long commentId, String text, LocalDateTime createdAt, Long userId) {
}
