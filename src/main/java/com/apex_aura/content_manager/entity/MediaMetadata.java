package com.apex_aura.content_manager.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "media_metadata")
@Data
public class MediaMetadata {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long mediaMetadataId;

    @OneToOne
    @JoinColumn(name = "content_id", nullable = false)
    @JsonIgnore
    private Content content;

    private String mimeType;// type of the media file
    private String duration; // hh:mm:ss for audio/video
    private Integer pageCount; // for PDFs
    private String resolution; // for video
    private String thumbnailUrl; // for audio/video/blog
}
