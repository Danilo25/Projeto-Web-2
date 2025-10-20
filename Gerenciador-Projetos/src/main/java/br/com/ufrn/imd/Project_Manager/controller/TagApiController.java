package br.com.ufrn.imd.Project_Manager.controller;

import br.com.ufrn.imd.Project_Manager.dtos.TagRequest;
import br.com.ufrn.imd.Project_Manager.dtos.TagResponse;
import br.com.ufrn.imd.Project_Manager.service.TagService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/tags")
@RequiredArgsConstructor
public class TagApiController {

    private final TagService tagService;

    @PostMapping("/create")
    public ResponseEntity<String> createTag(@RequestBody TagRequest tagRequest) {
        TagResponse tagResponse =  this.tagService.createTag(tagRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(tagResponse.toString());
    }

    @GetMapping("/list")
    public ResponseEntity<List<TagResponse>> listTags() {
        List<TagResponse> tags = this.tagService.listAllTags();

        if(tags.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(tags);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TagResponse> getTagById(@PathVariable String id) {
        TagResponse tag = this.tagService.findByName(id);

        if(tag == null) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(tag);
    }

    @PutMapping("/{id}")
    public ResponseEntity<String> updateTag(@PathVariable String id, @RequestBody TagRequest tagRequest) {
        TagResponse tagResponse = this.tagService.updateTag(id, tagRequest);
        return ResponseEntity.status(HttpStatus.OK).body(tagResponse.toString());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteTag(@PathVariable String id) {
        this.tagService.deleteTag(id);
        return ResponseEntity.noContent().build();
    }
}
