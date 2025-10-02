package com.apex_aura.content_manager.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "folder_access")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FolderAccess {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long folderAccessId;

    @ManyToOne
    @JoinColumn(name = "folder_id", nullable = false)
    private Folder folder;

    @Column(nullable = false)
    private Long userId; // from Profiler (JWT subject)

    @Column(nullable = false)
    private LocalDateTime grantedAt = LocalDateTime.now();

    @Column(nullable = false)
    private LocalDateTime expiresAt; // access expiration

    @Column
    private Boolean hasPaid = false; // true if user paid for folder

    @Column
    private LocalDateTime paymentDate;
}