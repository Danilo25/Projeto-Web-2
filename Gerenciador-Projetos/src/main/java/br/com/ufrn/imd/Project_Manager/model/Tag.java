package br.com.ufrn.imd.Project_Manager.model;

import br.com.ufrn.imd.Project_Manager.dtos.api.TagRequest;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@Entity
@NoArgsConstructor
public class Tag {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    public Tag(TagRequest tagRequest) {
        this.name = tagRequest.name();
    }
}
