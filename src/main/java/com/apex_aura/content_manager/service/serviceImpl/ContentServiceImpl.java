package com.apex_aura.content_manager.service.serviceImpl;

import com.apex_aura.content_manager.dto.ResponseDTO;
import com.apex_aura.content_manager.dto.request.ContentRequest;
import com.apex_aura.content_manager.dto.response.ContentResponse;
import com.apex_aura.content_manager.entity.*;
import com.apex_aura.content_manager.repository.ContentRepository;
import com.apex_aura.content_manager.repository.FolderAdminRepository;
import com.apex_aura.content_manager.repository.FolderRepository;
import com.apex_aura.content_manager.repository.MediaMetadataRepository;
import com.apex_aura.content_manager.service.ContentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ContentServiceImpl implements ContentService {

    private final ContentRepository contentRepository;
    private final FolderRepository folderRepository;
    private final MediaMetadataRepository mediaMetadataRepository;
    private final FolderAdminRepository folderAdminRepository;

    @Override
    public ResponseDTO addContent(ContentRequest req) {
        try {
            Folder folder = folderRepository.findById(req.getFolderId())
                    .orElseThrow(() -> new RuntimeException("Folder not found"));

            // Fetch folder admins
            Set<Long> folderAdminIds = folderAdminRepository.findAllByFolder_FolderId(req.getFolderId())
                    .stream()
                    .map(FolderAdmin::getUserId)
                    .collect(Collectors.toSet());

            if (!folderAdminIds.contains(req.getRequestingUserId())) {
                return ResponseDTO.builder()
                        .status("FAILURE")
                        .responseCode(403)
                        .message("Unauthorized access: User is not a folder admin")
                        .build();
            }

            // Create Content
            Content content = new Content();
            content.setFolder(folder);
            content.setType(req.getType());
            content.setTitle(req.getTitle());
            content.setDescription(req.getDescription());
            content.setFileUrl(req.getFileUrl());
            content.setTextContent(req.getTextContent());
            content.setSizeInBytes(req.getSizeInBytes());

            // Create MediaMetadata
            MediaMetadata metadata = new MediaMetadata();
            metadata.setMimeType(req.getMimeType());
            metadata.setDuration(req.getDuration());
            metadata.setPageCount(req.getPageCount());
            metadata.setResolution(req.getResolution());
            metadata.setThumbnailUrl(req.getThumbnailUrl());

            content.setMediaMetadata(metadata);
            metadata.setContent(content);

            Content savedContent = contentRepository.save(content);
            return ResponseDTO.builder()
                    .status("SUCCESS")
                    .message("Content created successfully")
                    .responseCode(2000)
                    .data(savedContent.getContentId())
                    .build();

        } catch (Exception e) {
            return ResponseDTO.builder()
                    .status("FAILURE")
                    .responseCode(5000)
                    .message("Error adding content: " + e.getMessage())
                    .build();
        }
    }

    @Override
    public ResponseDTO getContentById(Long contentId) {
        try {
            Content content = contentRepository.findById(contentId)
                    .orElseThrow(() -> new RuntimeException("Content not found"));

            Optional<MediaMetadata> metadataOptional = mediaMetadataRepository.findByContent(content);

            MediaMetadata metadata = metadataOptional.orElse(null);

            ContentResponse response = ContentResponse.builder()
                    .contentId(content.getContentId())
                    .folderId(content.getFolder().getFolderId())
                    .type(content.getType())
                    .title(content.getTitle())
                    .description(content.getDescription())
                    .fileUrl(content.getFileUrl())
                    .textContent(content.getTextContent())
                    .sizeInBytes(content.getSizeInBytes())
                    .createdAt(content.getCreatedAt().format(DateTimeFormatter.ISO_OFFSET_DATE_TIME))
                    .mimeType(metadata != null ? metadata.getMimeType() : null)
                    .duration(metadata != null ? metadata.getDuration() : null)
                    .pageCount(metadata != null ? metadata.getPageCount() : null)
                    .resolution(metadata != null ? metadata.getResolution() : null)
                    .thumbnailUrl(metadata != null ? metadata.getThumbnailUrl() : null)
                    .build();

            return ResponseDTO.builder()
                    .status("SUCCESS")
                    .message("Content fetched successfully")
                    .data(response)
                    .responseCode(2000)
                    .build();

        } catch (Exception e) {
            return ResponseDTO.builder()
                    .status("FAILURE")
                    .responseCode(5000)
                    .message("Error fetching content: " + e.getMessage())
                    .build();
        }
    }
}
