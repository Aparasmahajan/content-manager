package com.apex_aura.content_manager.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.ZoneId;
import java.time.ZonedDateTime;

@Entity
@Table(name = "reported_content")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ReportedContent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long reportId;

    private String userId;

    @ManyToOne
    @JoinColumn(name = "content_id")
    private Content content;

    @ManyToOne
    @JoinColumn(name = "comment_id")
    private Comment comment;

    @Column(columnDefinition = "TEXT")
    private String reason;

    private String status = "OPEN";

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

    private Boolean isActive = true;
}
