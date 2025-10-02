package com.apex_aura.content_manager.dto.response;

import com.apex_aura.content_manager.entity.ContentType;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ContentResponse {
    private Long contentId;
    private Long folderId;
    private ContentType type;
    private String title;
    private String description;
    private String fileUrl;
    private String textContent;
    private Long sizeInBytes;
    private String createdAt;

    // Media metadata
    private String mimeType;
    private String duration;
    private Integer pageCount;
    private String resolution;
    private String thumbnailUrl;
}
