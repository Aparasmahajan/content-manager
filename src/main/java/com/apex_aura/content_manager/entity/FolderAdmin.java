package com.apex_aura.content_manager.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "folder_admins")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FolderAdmin {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @Column(nullable = false)
    private Long userId;

    @ManyToOne
    @JoinColumn(name = "folder_id", nullable = false)
    private Folder folder;
}
