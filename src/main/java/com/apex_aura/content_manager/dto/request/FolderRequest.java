package com.apex_aura.content_manager.dto.request;

import lombok.Data;

import java.util.Set;

@Data
public class FolderRequest {
    private String portalName;
    private String name;
    private Long parentFolderId;
    private Boolean isUniversal = false;
    private Double price;
    private String description;
    private Integer accessDurationInDays;
    private Long createdByUserId; // admin creating folder
    private Boolean isRoot = true;
    private Set<Long> userIds;
}