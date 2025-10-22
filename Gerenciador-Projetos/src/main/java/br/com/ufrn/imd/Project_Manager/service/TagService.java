package br.com.ufrn.imd.Project_Manager.service;

import br.com.ufrn.imd.Project_Manager.dtos.TagRequest;
import br.com.ufrn.imd.Project_Manager.dtos.TagResponse;
import br.com.ufrn.imd.Project_Manager.model.Tag;
import br.com.ufrn.imd.Project_Manager.repository.TagRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class TagService {

    @Autowired
    private TagRepository tagRepository;
    /*
    @Autowired
    private TaskRepository taskRepository;
     */

    public List<TagResponse> findByName(String tagName) {
       List<Tag> tags = this.tagRepository.findByNameIgnoreCase(tagName);

       return tags.stream().map(e -> new TagResponse(e.getId(), e.getName())).toList();
    }

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
    public void addTagToTask(){}

    @Transactional
    public void removeTagFromTask(){}

    @Transactional
    public void findByTask(Long taskId) {}
}
