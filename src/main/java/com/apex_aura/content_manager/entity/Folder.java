package com.apex_aura.content_manager.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Set;
import java.util.HashSet;

@Entity
@Table(name = "folders")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Folder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long folderId;

    @Column(nullable = false)
    private Long portalId;

    @Column(nullable = false)
    private String name;

    @ManyToOne
    @JoinColumn(name = "parent_folder_id")
    @JsonIgnore
    private Folder parentFolder;

    @OneToMany(mappedBy = "parentFolder", cascade = CascadeType.ALL)
    private Set<Folder> subFolders = new HashSet<>();

    @OneToMany(mappedBy = "folder", cascade = CascadeType.ALL)
    private Set<Content> contents = new HashSet<>();

    @Column(nullable = false)
    private Boolean isRoot = true;

    @Column(nullable = false)
    private Boolean isUniversal = true; // true if all users can access

    private Double price; // if restricted folder has a paywall

    @OneToMany(mappedBy = "folder", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<FolderAccess> folderAccessList = new HashSet<>();

    //add owner of various folders
    @OneToMany(mappedBy = "folder", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<FolderAdmin> admins = new HashSet<>();

    String description;

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

    @Column
    private Integer accessDurationInDays; // optional, null = unlimited

}
