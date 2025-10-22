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

    @GetMapping
    public ResponseEntity<List<TagResponse>> listTags() {
        List<TagResponse> tags = this.tagService.listAllTags();

        if(tags.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(tags);
    }

    @GetMapping("/{name}")
    public ResponseEntity<List<TagResponse>> getTagById(@PathVariable String name) {
        List<TagResponse> tags = this.tagService.findByName(name);

        if(tags.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(tags);
    }

    @PutMapping("/{id}")
    public ResponseEntity<String> updateTag(@PathVariable Long id, @RequestBody TagRequest tagRequest) {
        TagResponse tagResponse = this.tagService.updateTag(id, tagRequest);
        return ResponseEntity.status(HttpStatus.OK).body(tagResponse.toString());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteTag(@PathVariable Long id) {
        this.tagService.deleteTag(id);
        return ResponseEntity.noContent().build();
    }
}
