package br.com.ufrn.imd.Project_Manager.model;

import java.util.HashSet;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

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
    private String position;

    @ManyToMany(mappedBy = "users")
    @JsonIgnore
    private Set<Team> teams = new HashSet<>();

    public User() {
    }

}
