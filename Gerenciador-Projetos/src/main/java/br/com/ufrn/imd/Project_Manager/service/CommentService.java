package br.com.ufrn.imd.Project_Manager.service;

import br.com.ufrn.imd.Project_Manager.dtos.api.CommentRequest;
import br.com.ufrn.imd.Project_Manager.dtos.api.CommentResponse;
import br.com.ufrn.imd.Project_Manager.model.Comment;
import br.com.ufrn.imd.Project_Manager.model.Task;
import br.com.ufrn.imd.Project_Manager.model.User;
import br.com.ufrn.imd.Project_Manager.repository.CommentRepository;
import br.com.ufrn.imd.Project_Manager.repository.TaskRepository;
import br.com.ufrn.imd.Project_Manager.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CommentService {

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private UserRepository userRepository;

    public CommentResponse toCommentResponse(Comment comment) {
        return new CommentResponse(
                comment.getId(),
                comment.getText(),
                comment.getCreatedAt(),
                comment.getUser() != null ? comment.getUser().getId() : null
        );
    }

    public List<CommentResponse> searchComments(Long taskId, String text) {
        List<Comment> comments = this.commentRepository.searchComments(taskId, text);
        return comments.stream().map(this::toCommentResponse).toList();
    }

    public CommentResponse getCommentById(Long commentId) {
        Comment comment = this.commentRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("Comment not found!"));

        return toCommentResponse(comment);
    }

    @Transactional
    public CommentResponse createComment(CommentRequest commentRequest){
        Task task = this.taskRepository.findById(commentRequest.taskId())
                .orElseThrow(() -> new RuntimeException("Task not found!"));
        User user = this.userRepository.findById(commentRequest.userId())
                .orElseThrow(() -> new RuntimeException("User not found!"));

        Comment newComment = new Comment(
                commentRequest.text(),
                commentRequest.createdAt(),
                task,
                user
        );

        Comment savedComment = this.commentRepository.save(newComment);
        return toCommentResponse(savedComment);
    }

    @Transactional
    public CommentResponse updateComment(Long id, CommentRequest commentRequest){
        Comment existingComment = this.commentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Comment not found!"));

        if (commentRequest.text() != null) {
            existingComment.setText(commentRequest.text());
        }
        if (commentRequest.createdAt() != null) {
            existingComment.setCreatedAt(commentRequest.createdAt());
        }
        if (commentRequest.userId() != null) {
            User user = this.userRepository.findById(commentRequest.userId())
                    .orElseThrow(() -> new RuntimeException("User not found!"));
            existingComment.setUser(user);
        }
        if (commentRequest.taskId() != null) {
            Task task = this.taskRepository.findById(commentRequest.taskId())
                    .orElseThrow(() -> new RuntimeException("Task not found!"));
            existingComment.setTask(task);
        }

        Comment updatedComment = this.commentRepository.save(existingComment);
        return toCommentResponse(updatedComment);
    }

    @Transactional
    public void deleteComment(Long id){
        Comment existingComment = this.commentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Comment not found!"));
        this.commentRepository.delete(existingComment);
    }
}
