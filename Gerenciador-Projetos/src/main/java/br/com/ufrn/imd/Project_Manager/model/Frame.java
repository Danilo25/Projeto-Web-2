package br.com.ufrn.imd.Project_Manager.model;

import jakarta.persistence.*;
import java.util.List;

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

    public Integer getOrderIndex() {
        return orderIndex;
    }

    public void setOrderIndex(Integer orderIndex) {
        this.orderIndex = orderIndex;
    }

    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project = project;
    }

    public List<Task> getTasks() {
        return tasks;
    }

    public void setTasks(List<Task> tasks) {
        this.tasks = tasks;
    }
}
