package com.apex_aura.content_manager.repository;

import com.apex_aura.content_manager.entity.Folder;
import com.apex_aura.content_manager.entity.FolderAdmin;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FolderAdminRepository extends JpaRepository<FolderAdmin, Long> {
    List<FolderAdmin> findAllByFolder_FolderId(Long folderId);

    Optional<FolderAdmin> findByFolder(Folder folder);
}