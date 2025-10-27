package br.com.ufrn.imd.Project_Manager.controller;

import br.com.ufrn.imd.Project_Manager.dtos.FrameRequest;
import br.com.ufrn.imd.Project_Manager.dtos.FrameResponse;
import br.com.ufrn.imd.Project_Manager.service.FrameService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/frames")
public class FrameApiController {

    @Autowired
    private FrameService frameService;

    @GetMapping
    public ResponseEntity<List<FrameResponse>> getAllFrames() {
        List<FrameResponse> frames = frameService.listAllFrames();
        return ResponseEntity.ok().body(frames);
    }

    @GetMapping("/{id}")
    public ResponseEntity<FrameResponse> getFrameById(@PathVariable Long id) {
        FrameResponse frame = frameService.getFrameById(id);
        return ResponseEntity.ok().body(frame);
    }

    @GetMapping("/search/{name}")
    public ResponseEntity<List<FrameResponse>> getFramesByName(@PathVariable String name) {
        List<FrameResponse> frames = frameService.findByName(name);
        return ResponseEntity.ok().body(frames);
    }

    @PostMapping("/{id}")
    public ResponseEntity<FrameResponse> createFrame(@RequestBody FrameRequest frame) {
        FrameResponse newFrame = frameService.createFrame(frame);
        return ResponseEntity.ok().body(newFrame);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<FrameResponse> updateFrame(@PathVariable Long id, @RequestBody FrameRequest frame) {
        FrameResponse updatedFrame = frameService.updateFrame(id, frame);
        return ResponseEntity.ok().body(updatedFrame);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFrame(@PathVariable Long id) {
        frameService.deleteFrame(id);
        return ResponseEntity.noContent().build();
    }
}
