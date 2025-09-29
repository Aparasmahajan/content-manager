package com.apex_aura.content_manager.entity;


import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

@Entity
@Table(name = "contents")
public class Content {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "content_id")
    private Long contentId;

    @ManyToOne
    @JoinColumn(name = "folder_id", nullable = false)
    private Folder folder;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ContentType type; // BLOG, PDF, AUDIO, VIDEO

    @Column(nullable = false)
    private String title;

    @Lob
    private String description; // optional - for blog or metadata

    private String fileUrl; // for PDF, audio, video storage path

    @Lob
    private String textContent; // for blog content (can be Markdown/HTML)

    private Long sizeInBytes;

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
}
