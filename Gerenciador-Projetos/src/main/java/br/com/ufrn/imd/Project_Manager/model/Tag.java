package br.com.ufrn.imd.Project_Manager.model;

import br.com.ufrn.imd.Project_Manager.dtos.TagRequest;
import jakarta.persistence.*;

@Entity
public class Tag {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    public Tag(TagRequest tagRequest) {
        this.name = tagRequest.name();
    }

    public Tag() {

    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
