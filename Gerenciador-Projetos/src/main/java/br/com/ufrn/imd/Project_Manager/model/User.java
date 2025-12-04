package br.com.ufrn.imd.Project_Manager.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Setter
@Getter
@Entity
@Table(name = "app_user")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String email;
    private String password;

    @ManyToOne
    @JoinColumn(name = "position_id")
    private Position position;

    @ManyToMany(mappedBy = "users")
    @JsonIgnore
    private Set<Team> teams = new HashSet<>();

    public User() {
    }

    public User(String name, String email, String password, Position position) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.position = position;
    }

    public User(String name, String email, String password) {
        this.name = name;
        this.email = email;
        this.password = password;
    }

}
