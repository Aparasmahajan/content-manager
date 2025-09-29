package com.apex_aura.content_manager.dto.response;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class FolderResponse {
    private Long folderId;
    private String name;
    private Long portalId;
    private String description;
    private Boolean isUniversal;
    private Double price;
    private Integer accessDurationInDays;
    private List<Long> adminUserIds;
    private Boolean isRoot=false;
    private List<FolderResponse> subFolders;   // recursive subfolders
    private List<Long> contents;               // list of content IDs
}