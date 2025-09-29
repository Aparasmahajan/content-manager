package com.apex_aura.content_manager.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;

@Entity
@Table(name = "content_comments")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long commentId;

    @ManyToOne
    @JoinColumn(name = "content_id")
    @JsonIgnore
    private Content content;

    private String userId;

    @ManyToOne
    @JoinColumn(name = "parent_comment_id")
    private Comment parentComment;

    @OneToMany(mappedBy = "parentComment", cascade = CascadeType.ALL)
    private List<Comment> replies;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String text;


    @Column(nullable = false, updatable = false)
    private ZonedDateTime createdAt;

    @Column
    private ZonedDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        ZoneId indiaZone = ZoneId.of("Asia/Kolkata"); // GMT+5:30
        this.createdAt = ZonedDateTime.now(indiaZone);
        this.updatedAt = ZonedDateTime.now(indiaZone);
    }

    @PreUpdate
    protected void onUpdate() {
        ZoneId indiaZone = ZoneId.of("Asia/Kolkata"); // GMT+5:30
        this.updatedAt = ZonedDateTime.now(indiaZone);
    }

    private Boolean isDeleted = false;
    private Boolean isActive = true;
}

