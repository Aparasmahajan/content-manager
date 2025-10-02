package com.apex_aura.content_manager.dto.request;

import com.apex_aura.content_manager.entity.ContentType;
import lombok.Data;

@Data
public class ContentRequest {
    private Long folderId;
    private ContentType type;
    private String title;
    private String description;
    private String fileUrl;
    private String textContent;
    private Long sizeInBytes;

    // Media metadata fields
    private String mimeType;
    private String duration;
    private Integer pageCount;
    private String resolution;
    private String thumbnailUrl;

    private Long requestingUserId;
}
