package br.com.ufrn.imd.Project_Manager.service;

import br.com.ufrn.imd.Project_Manager.dtos.CommentRequest;
import br.com.ufrn.imd.Project_Manager.dtos.CommentResponse;
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
import java.util.Optional;

@Service
public class CommentService {

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private UserRepository userRepository;

    public void saveComment(Comment comment){
        this.commentRepository.save(comment);
    }

    @Transactional(readOnly = true)
    public List<CommentResponse> listAllComments(){
        return this.commentRepository.findAll().stream().map(comment -> new CommentResponse(comment.getId(), comment.getText(), comment.getCreatedAt(), comment.getUser().getId())).toList();
    }

    @Transactional(readOnly = true)
    public List<CommentResponse> findByName(String commentText) {
        List<Comment> comments = this.commentRepository.findByTextContainingIgnoreCase(commentText);

        return comments.stream().map(comment -> new CommentResponse(comment.getId(), comment.getText(), comment.getCreatedAt(), comment.getUser().getId())).toList();
    }

    @Transactional
    public CommentResponse CreateComment(CommentRequest commentRequest){
        Optional<Task> task = this.taskRepository.findById(commentRequest.taskId());
        Optional<User> user = this.userRepository.findById(commentRequest.userId());

        if(task.isPresent() && user.isPresent()){
            Comment newComment = new Comment(commentRequest);
            newComment.setUser(user.get());
            newComment.setTask(task.get());
            this.saveComment(newComment);
            return new CommentResponse(newComment.getId(), newComment.getText(), newComment.getCreatedAt(), newComment.getUser().getId());
        }
        else{
            throw new RuntimeException("User or Task Not Found!");
        }
    }

    @Transactional
    public CommentResponse UpdateComment(Long id, CommentRequest commentRequest){
        Comment foundComment = this.commentRepository.findById(id).orElseThrow(() -> new RuntimeException("Comment not found!"));

        foundComment.setText(commentRequest.text());
        foundComment.setCreatedAt(commentRequest.createdAt());
        this.saveComment(foundComment);

        return new CommentResponse(foundComment.getId(), foundComment.getText(), foundComment.getCreatedAt(),  foundComment.getUser().getId());
    }

    @Transactional
    public void DeleteComment(Long id){
        Comment foundComment = this.commentRepository.findById(id).orElseThrow(() -> new RuntimeException("Comment not found!"));
        this.commentRepository.delete(foundComment);
    }

    @Transactional(readOnly = true)
    public List<CommentResponse> findCommentByTask(Long taskId) {
        Optional<Task> task = this.taskRepository.findById(taskId);

        if(task.isPresent()){
            if(task.get().getComments().isEmpty()){
                return List.of();
            }
            else{
                return task.get().getComments().stream().map(comment -> new CommentResponse(comment.getId(), comment.getText(), comment.getCreatedAt(), comment.getUser().getId())).toList();
            }
        }
        else{
            throw new RuntimeException("Task not found!");
        }
    }

}
