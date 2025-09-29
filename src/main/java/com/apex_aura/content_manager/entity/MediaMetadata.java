package com.apex_aura.content_manager.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "media_metadata")
public class MediaMetadata {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long mediaMetadataId;

    @OneToOne
    @JoinColumn(name = "content_id", nullable = false)
    private Content content;

    private String mimeType;// type of the media file
    private String duration; // hh:mm:ss for audio/video
    private Integer pageCount; // for PDFs
    private String resolution; // for video
}
