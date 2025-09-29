package com.apex_aura.content_manager.repository;

import com.apex_aura.content_manager.entity.Folder;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FolderRepository extends JpaRepository<Folder, Long> {
}