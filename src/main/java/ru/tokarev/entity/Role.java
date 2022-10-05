package ru.tokarev.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.io.Serializable;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "role")
public class Role implements Serializable {

    @Id
    @SequenceGenerator(name = "role_seq", sequenceName = "role_id_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "role_seq")
    @NotBlank(message = "Id is mandatory")
    private Long id;

    @Column(name = "name")
    @NotBlank(message = "Name is mandatory")
    private String name;

    @Override
    public String toString() {
        return "Role{" +
                "id: " + id + ", " +
                "name: " + name + "}";
    }
}
