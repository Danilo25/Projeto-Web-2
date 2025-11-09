package br.com.ufrn.imd.Project_Manager.service;

import br.com.ufrn.imd.Project_Manager.dtos.api.TagRequest;
import br.com.ufrn.imd.Project_Manager.dtos.api.TagResponse;
import br.com.ufrn.imd.Project_Manager.model.Tag;
import br.com.ufrn.imd.Project_Manager.model.Task;
import br.com.ufrn.imd.Project_Manager.repository.TagRepository;
import br.com.ufrn.imd.Project_Manager.repository.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class TagService {

    @Autowired
    private TagRepository tagRepository;

    @Autowired
    private TaskRepository taskRepository;

    public TagResponse toTagResponse(Tag tag) {
        return new TagResponse(tag.getId(), tag.getName());
    }

    public List<TagResponse> getTags(String name){
        List<Tag> tags = this.tagRepository.searchTags(name);
        return tags.stream().map(this::toTagResponse).toList();
    }

    public TagResponse getTagById(Long id) {
        Tag tag = this.tagRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Tag not found!"));
        return toTagResponse(tag);
    }

    @Transactional(readOnly = true)
    public List<TagResponse> findByName(String tagName) {
       List<Tag> tags = this.tagRepository.findByNameIgnoreCase(tagName);

       return tags.stream().map(e -> new TagResponse(e.getId(), e.getName())).toList();
    }

    public void saveTag(Tag tag) {
        this.tagRepository.save(tag);
    }

    @Transactional
    public TagResponse createTag(TagRequest data) {
        boolean exists = this.tagRepository.existsByNameIgnoreCase(data.name());
        if(exists){
            throw new RuntimeException("Already exists!");
        }

        Tag newTag = new Tag(data);
        this.saveTag(newTag);

        return new TagResponse(newTag.getId(), newTag.getName());
    }

    @Transactional
    public TagResponse updateTag(Long id, TagRequest data) {
        Tag foundTag = this.tagRepository.findById(id).orElseThrow(() -> new RuntimeException("Tag not found!"));

        foundTag.setName(data.name());
        this.saveTag(foundTag);

        return new TagResponse(foundTag.getId(), foundTag.getName());
    }

    @Transactional
    public void deleteTag(Long id) {
        Tag foundTag = this.tagRepository.findById(id).orElseThrow(() -> new RuntimeException("Tag not found!"));
        this.tagRepository.delete(foundTag);
    }

    @Transactional
    public void addTagToTask(Long tagId, Long taskId) {
        Task task = this.taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Task not found!"));
        Tag tag = this.tagRepository.findById(tagId)
                .orElseThrow(() -> new RuntimeException("Tag not found!"));

        if (task.getTags().contains(tag)){
            throw  new RuntimeException("Tag already associated!");
        }
        this.taskRepository.save(task);
    }

    @Transactional
    public void removeTagFromTask(Long tagId, Long taskId) {
        Task task = this.taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Task not found!"));
        Tag tag = this.tagRepository.findById(tagId)
                .orElseThrow(() -> new RuntimeException("Tag not found!"));

        if(task.getTags().contains(tag)){
            task.getTags().remove(tag);
            this.taskRepository.save(task);
        }
        else{
            throw  new RuntimeException("Tag not associated!");
        }
    }

    @Transactional(readOnly = true)
    public List<TagResponse> findTagsByTask(Long taskId) {
        Task task = this.taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Task not found!"));

        return task.getTags().stream().map(this::toTagResponse).toList();
    }
}
