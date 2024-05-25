package com.bobocode.demo.entity;

import com.bobocode.bibernate.annotation.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table("skills")
@Data
@NoArgsConstructor
public class Skill {
    @Id
    private Integer id;
    
    private String title;

    private String level;

    @Column(value = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @ManyToOne
    @JoinColumn("participant_id")
    private Participant participant;
}