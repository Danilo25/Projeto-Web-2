package br.com.ufrn.imd.Project_Manager.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
@Entity
public class Frame {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private Integer orderIndex;

    @ManyToOne
    @JoinColumn(name = "project_id")
    private Project project;

    @OneToMany(mappedBy = "frame", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Task> tasks;

    public Frame() {
    }

    public Frame(String name, Integer orderIndex) {
        this.name = name;
        this.orderIndex = orderIndex;
    }

    public Frame(String name, Integer orderIndex, Project project) {
        this.name = name;
        this.orderIndex = orderIndex;
        this.project = project;
    }

}
