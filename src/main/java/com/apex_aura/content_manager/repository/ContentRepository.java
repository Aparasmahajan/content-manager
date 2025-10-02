package com.apex_aura.content_manager.repository;

import com.apex_aura.content_manager.entity.Content;
import com.apex_aura.content_manager.entity.Folder;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ContentRepository extends JpaRepository<Content, Long> {
    List<Content> findByFolder_FolderId(Long folderId);
}