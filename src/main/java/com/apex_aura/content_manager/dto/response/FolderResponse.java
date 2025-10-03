package com.apex_aura.content_manager.dto.response;

import com.apex_aura.content_manager.entity.Content;
import com.apex_aura.content_manager.entity.FolderAccess;
import com.apex_aura.content_manager.entity.FolderAdmin;
import com.apex_aura.content_manager.entity.MediaMetadata;
import lombok.Builder;
import lombok.Data;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;
@Data
@Builder
public class FolderResponse {
    private Long folderId;
    private Long portalId;
    private String name;
    private String description;
    private Boolean isUniversal;
    private Double price;
    private Integer accessDurationInDays;
    private Boolean isRoot;
    private List<Long> adminUserIds;            // IDs of folder admins
    private Set<FolderResponse> subFolders;          // recursive subfolders
    private List<Content> contents;                  // full content objects
    private List<FolderAdmin> admins;                // full admin objects
    private Set<FolderAccess> folderAccessList;     // access list
    private List<MediaMetadata> mediaMetadataList;  // media metadata

    private Boolean canEdit;
    private ZonedDateTime createdAt;
}