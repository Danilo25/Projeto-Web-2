package br.com.ufrn.imd.Project_Manager.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
@Entity
public class Project {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String description;
    private LocalDate initialDate;
    private LocalDate finalDate;
    private String status;

    @ManyToOne
    @JoinColumn(name = "team_id", nullable = false)
    private Team team;

    @OneToMany(mappedBy = "project")
    private List<Frame> frames;

    public Project() {
    }

    public Project(String name, String description, LocalDate initialDate, LocalDate finalDate, String status, Team team) {
        this.name = name;
        this.description = description;
        this.initialDate = initialDate;
        this.finalDate = finalDate;
        this.status = status;
        this.team = team;
        this.frames = new ArrayList<>();
    }
}
