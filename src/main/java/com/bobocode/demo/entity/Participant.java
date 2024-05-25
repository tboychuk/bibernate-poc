package com.bobocode.demo.entity;

import com.bobocode.bibernate.annotation.Column;
import com.bobocode.bibernate.annotation.Entity;
import com.bobocode.bibernate.annotation.Id;
import com.bobocode.bibernate.annotation.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

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

    private Integer yearsOfExperience;

    private LocalDateTime createdAt;
}