package com.apex_aura.content_manager.entity;


import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "contents")
public class Content {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

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

    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    private LocalDateTime updatedAt;
}
