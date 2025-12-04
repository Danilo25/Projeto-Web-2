package br.com.ufrn.imd.Project_Manager.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
@Entity
@Table(uniqueConstraints = {@UniqueConstraint(columnNames = {"name", "level"})})
public class Position {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String level;

    private String description;

    @OneToMany(mappedBy = "position")
    private List<User> users = new ArrayList<>();

    public Position() {
    }

    public Position(String name, String level, String description) {
        this.name = name;
        this.level = level;
        this.description = description;
    }
}
