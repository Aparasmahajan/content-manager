package com.apex_aura.content_manager.repository;

import com.apex_aura.content_manager.entity.Folder;
import com.apex_aura.content_manager.entity.FolderAccess;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FolderAccessRepository extends JpaRepository<FolderAccess, Long> {
}