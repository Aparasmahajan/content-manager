package com.apex_aura.content_manager.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
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
    private Long id;

    @Column(nullable = false)
    private Long portalId;

    @Column(nullable = false)
    private String name;

    @ManyToOne
    @JoinColumn(name = "parent_folder_id")
    private Folder parentFolder;

    @OneToMany(mappedBy = "parentFolder", cascade = CascadeType.ALL)
    private Set<Folder> subFolders = new HashSet<>();

    @OneToMany(mappedBy = "folder", cascade = CascadeType.ALL)
    private Set<Content> contents = new HashSet<>();

    @Column(nullable = false)
    private Boolean isRoot = false;

    @Column(nullable = false)
    private Boolean isUniversal = false; // true if all users can access

    private Double price; // if restricted folder has a paywall

    @OneToMany(mappedBy = "folder", cascade = CascadeType.ALL)
    private Set<FolderAccess> folderAccessList = new HashSet<>();

    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    private LocalDateTime updatedAt;

    @Column
    private Integer accessDurationInDays; // optional, null = unlimited

}
