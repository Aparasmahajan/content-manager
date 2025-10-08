package com.apex_aura.content_manager.dto.request;

import lombok.Data;

import java.util.List;
import java.util.Set;

@Data
public class FolderAccessRequest {
    private Long folderId;
    private Set<Long> userIds;
}