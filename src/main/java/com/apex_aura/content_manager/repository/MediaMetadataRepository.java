package com.apex_aura.content_manager.repository;

import com.apex_aura.content_manager.entity.Content;
import com.apex_aura.content_manager.entity.MediaMetadata;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MediaMetadataRepository extends JpaRepository<MediaMetadata, Long> {
    Optional<MediaMetadata> findByContent(Content content);
}