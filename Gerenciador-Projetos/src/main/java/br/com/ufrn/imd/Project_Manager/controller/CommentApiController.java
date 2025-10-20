package br.com.ufrn.imd.Project_Manager.controller;

import br.com.ufrn.imd.Project_Manager.dtos.CommentRequest;
import br.com.ufrn.imd.Project_Manager.dtos.CommentResponse;
import br.com.ufrn.imd.Project_Manager.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("comments")
public class CommentApiController {

    @Autowired
    private CommentService commentService;

    @GetMapping
    public ResponseEntity<List<CommentResponse>> findAllByTask() {
        List<CommentResponse> comments = this.commentService.listAllComments();

        if(comments.isEmpty()){
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(comments);
    }

    @GetMapping("{name}")
    public ResponseEntity<List<CommentResponse>> findByName(@PathVariable String name) {
        List<CommentResponse> comments = this.commentService.findByName(name);
        if(comments.isEmpty()){
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(comments);
    }

    @PostMapping("/create")
    public ResponseEntity<CommentResponse> createComment(@RequestBody CommentRequest commentRequest) {
        CommentResponse commentResponse = this.commentService.CreateComment(commentRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(commentResponse);
    }

    @PutMapping("{id}")
    public ResponseEntity<CommentResponse> updateComment(@PathVariable Long id, @RequestBody CommentRequest commentRequest) {
        CommentResponse commentResponse = this.commentService.UpdateComment(id, commentRequest);
        return ResponseEntity.ok(commentResponse);
    }

    @DeleteMapping("{id}")
    public ResponseEntity<CommentResponse> deleteComment(@PathVariable Long id) {
        this.commentService.DeleteComment(id);
        return ResponseEntity.noContent().build();
    }
}
