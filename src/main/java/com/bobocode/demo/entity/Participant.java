package com.bobocode.demo.entity;

import com.bobocode.bibernate.annotation.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicUpdate;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table("participants")
@Data
@NoArgsConstructor
public class Participant {
    @Id
    private Integer id;

    @Column("first_name")
    private String firstName;

    private String lastName; // last_name

    private String city;

    private String company;

    private String position;

    private Integer yearsOfExperience;

    @Column(value = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "participant")
    private List<Skill> skills = new ArrayList<>();
}