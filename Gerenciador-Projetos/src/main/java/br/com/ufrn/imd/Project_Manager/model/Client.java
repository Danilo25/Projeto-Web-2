package br.com.ufrn.imd.Project_Manager.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Entity
@Table(uniqueConstraints = {@UniqueConstraint(columnNames = {"name", "company", "email"})})
public class Client {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String company;
    private String email;
    private String phoneNumber;

    @OneToMany(mappedBy = "client")
    private List<Project> projects;

    public Client() {
    }

    public Client(String name, String company, String email, String phoneNumber) {
        this.name = name;
        this.company = company;
        this.email = email;
        this.phoneNumber = phoneNumber;
    }
}
