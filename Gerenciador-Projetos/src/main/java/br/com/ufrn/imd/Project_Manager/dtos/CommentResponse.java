package br.com.ufrn.imd.Project_Manager.dtos;

import java.time.LocalDateTime;

public record CommentResponse(Long id, String text, LocalDateTime createdAt) {
}
