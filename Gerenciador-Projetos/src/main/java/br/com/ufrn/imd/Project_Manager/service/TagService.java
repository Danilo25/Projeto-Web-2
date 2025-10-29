package br.com.ufrn.imd.Project_Manager.service;

import br.com.ufrn.imd.Project_Manager.dtos.TagRequest;
import br.com.ufrn.imd.Project_Manager.dtos.TagResponse;
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

    @Transactional(readOnly = true)
    public List<TagResponse> findByName(String tagName) {
       List<Tag> tags = this.tagRepository.findByNameIgnoreCase(tagName);

       return tags.stream().map(e -> new TagResponse(e.getId(), e.getName())).toList();
    }

    @Transactional(readOnly = true)
    public List<TagResponse> listAllTags(){
        return this.tagRepository.findAll().stream().map(e -> new TagResponse(e.getId(), e.getName())).toList();
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
        Optional<Task> task = this.taskRepository.findById(taskId);
        Optional<Tag> tag = this.tagRepository.findById(tagId);

        if(task.isPresent() && tag.isPresent()){
            if(task.get().getTags().contains(tag.get())){
                throw  new RuntimeException("Already exists!");
            }

            task.get().getTags().add(tag.get());
            this.taskRepository.save(task.get());
        }
    }

    @Transactional
    public void removeTagFromTask(Long tagId, Long taskId) {
        Optional<Task> task = this.taskRepository.findById(taskId);
        Optional<Tag> tag = this.tagRepository.findById(tagId);

        if(task.isPresent() && tag.isPresent()){
            if(task.get().getTags().contains(tag.get())){
                task.get().getTags().remove(tag.get());
                this.taskRepository.save(task.get());
            }
            else{
                throw  new RuntimeException("Unassociated tag!");
            }
        }
    }

    @Transactional(readOnly = true)
    public List<TagResponse> findTagsByTask(Long taskId) {
        Optional<Task> task = this.taskRepository.findById(taskId);

        if(task.isPresent()){
            if(task.get().getTags().isEmpty()){
                return List.of();
            }
            else{
                return task.get().getTags().stream().map(tag -> new TagResponse(tag.getId(), tag.getName())).toList();
            }
        }
        else{
            throw  new RuntimeException("Task not found!");
        }
    }
}
