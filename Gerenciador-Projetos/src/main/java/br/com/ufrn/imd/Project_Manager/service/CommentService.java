package br.com.ufrn.imd.Project_Manager.service;

import br.com.ufrn.imd.Project_Manager.dtos.CommentRequest;
import br.com.ufrn.imd.Project_Manager.dtos.CommentResponse;
import br.com.ufrn.imd.Project_Manager.model.Comment;
import br.com.ufrn.imd.Project_Manager.repository.CommentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CommentService {

    @Autowired
    private CommentRepository commentRepository;

    public void saveComment(Comment comment){
        this.commentRepository.save(comment);
    }

    public List<CommentResponse> listAllComments(){
        return this.commentRepository.findAll().stream().map(e -> new CommentResponse(e.getId(), e.getText(), e.getCreatedAt())).toList();
    }

    public List<CommentResponse> findByName(String commentText) {
        List<Comment> comments = this.commentRepository.findByTextContainingIgnoreCase(commentText);

        return comments.stream().map(e -> new CommentResponse(e.getId(), e.getText(), e.getCreatedAt())).toList();
    }

    @Transactional
    public CommentResponse CreateComment(CommentRequest commentRequest){
        Comment newComment = new Comment(commentRequest);
        this.saveComment(newComment);
        return new CommentResponse(newComment.getId(), newComment.getText(), newComment.getCreatedAt());
    }

    @Transactional
    public CommentResponse UpdateComment(Long id, CommentRequest commentRequest){
        Comment foundComment = this.commentRepository.findById(id).orElseThrow(() -> new RuntimeException("Comment not found!"));

        foundComment.setText(commentRequest.text());
        foundComment.setCreatedAt(commentRequest.createdAt());
        this.saveComment(foundComment);

        return new CommentResponse(foundComment.getId(), foundComment.getText(), foundComment.getCreatedAt());
    }

    @Transactional
    public void DeleteComment(Long id){
        Comment foundComment = this.commentRepository.findById(id).orElseThrow(() -> new RuntimeException("Comment not found!"));
        this.commentRepository.delete(foundComment);
    }

    @Transactional
    public void findByTask(Long taskId) {}

}
