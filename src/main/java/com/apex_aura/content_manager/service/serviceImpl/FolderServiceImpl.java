package com.apex_aura.content_manager.service.serviceImpl;

import com.apex_aura.content_manager.dto.AdminDto;
import com.apex_aura.content_manager.dto.PortalDto;
import com.apex_aura.content_manager.dto.ResponseDTO;
import com.apex_aura.content_manager.dto.request.FolderRequest;
import com.apex_aura.content_manager.dto.response.FolderResponse;
import com.apex_aura.content_manager.entity.Folder;
import com.apex_aura.content_manager.entity.FolderAdmin;
import com.apex_aura.content_manager.entity.MediaMetadata;
import com.apex_aura.content_manager.repository.FolderRepository;
import com.apex_aura.content_manager.repository.FolderAdminRepository;
import com.apex_aura.content_manager.repository.MediaMetadataRepository;
import com.apex_aura.content_manager.service.FolderService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FolderServiceImpl implements FolderService {
    private final FolderRepository folderRepository;
    private final FolderAdminRepository folderAdminRepository;
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    private final MediaMetadataRepository mediaMetadataRepository;

    @Value("${profiler.portalInfo.url}")
    String portalInfoUrl;

    @Override
    public ResponseDTO createFolder(FolderRequest req) {
        try {
            String profilerUrl = portalInfoUrl + req.getPortalName();
            ResponseDTO response = restTemplate.getForObject(profilerUrl, ResponseDTO.class);

            if (response == null || response.getData() == null) {
                return ResponseDTO.builder()
                        .status("FAILURE")
                        .message("Portal not found")
                        .responseCode(5000)
                        .build();
            }

            // Convert portal data to PortalDto
            PortalDto portalDto = objectMapper.convertValue(response.getData(), PortalDto.class);

            // Validate creator is portal admin
            List<Long> portalAdminIds = portalDto.getAdmins().stream()
                    .map(AdminDto::getUserId)
                    .toList();

            if (!portalAdminIds.contains(req.getCreatedByUserId())) {
                return ResponseDTO.builder()
                        .status("FAILURE")
                        .responseCode(5000)
                        .message("User is not an admin of this portal")
                        .build();
            }

            // Create folder
            Folder folder = Folder.builder()
                    .portalId(portalDto.getPortalId())  // use PortalDto directly
                    .name(req.getName())
                    .description(req.getDescription())
                    .isUniversal(req.getIsUniversal())
                    .price(req.getPrice())
                    .isRoot(req.getIsRoot() != null ? req.getIsRoot() : true)
                    .accessDurationInDays(req.getAccessDurationInDays())
                    .build();

            if (req.getParentFolderId() != null) {
                folderRepository.findById(req.getParentFolderId())
                        .ifPresent(folder::setParentFolder);
            }

            Folder saved = folderRepository.save(folder);

            // Combine portal admins + additional userIds from request
            Set<Long> folderAdminIds = new java.util.HashSet<>(portalAdminIds);
            if (req.getUserIds() != null) {
                folderAdminIds.addAll(req.getUserIds());
            }

            // Save all folder admins
            folderAdminIds.forEach(adminId -> {
                FolderAdmin admin = FolderAdmin.builder()
                        .userId(adminId)
                        .folder(saved)
                        .build();
                folderAdminRepository.save(admin);
            });

            FolderResponse folderResponse = FolderResponse.builder()
                    .folderId(saved.getFolderId())
                    .name(saved.getName())
                    .portalId(saved.getPortalId())
                    .description(saved.getDescription())
                    .isUniversal(saved.getIsUniversal())
                    .price(saved.getPrice())
                    .accessDurationInDays(saved.getAccessDurationInDays())
                    .adminUserIds(folderAdminIds.stream().toList())
                    .isRoot(saved.getIsRoot())
                    .build();

            return ResponseDTO.builder()
                    .status("SUCCESS")
                    .message("Folder created successfully")
                    .data(folderResponse)
                    .responseCode(2000)
                    .build();

        } catch (Exception e) {
            return ResponseDTO.builder()
                    .status("FAILURE")
                    .responseCode(5000)
                    .message("Error creating folder: " + e.getMessage())
                    .build();
        }
    }

    @Override
    public ResponseDTO getFolderDetails(Long folderId, HttpServletRequest request) {
        try {
            Long requestingUserId = Long.parseLong(request.getHeader("userId"));

            Folder folder = folderRepository.findById(folderId).orElse(null);
            if (folder == null) {
                return ResponseDTO.builder()
                        .status("FAILURE")
                        .message("Folder not found")
                        .responseCode(5001)
                        .build();
            }

            // Fetch folder admins from DB
            Set<Long> folderAdminIds = folderAdminRepository.findByFolder(folder)
                    .stream()
                    .map(FolderAdmin::getUserId)
                    .collect(Collectors.toSet());

            // Access check: super/admin/folderAdmin
            boolean hasAccess = folder.getIsUniversal() // universal folder
                    || folderAdminIds.contains(requestingUserId) // folder admin
                    || folder.getFolderAccessList().stream().anyMatch(access ->
                    access.getUserId().equals(requestingUserId)
                            && (access.getExpiresAt() == null || access.getExpiresAt().isAfter(LocalDateTime.now()))
            );

            if (!hasAccess) {
                return ResponseDTO.builder()
                        .status("FAILURE")
                        .message("Unauthorized: You do not have access to this folder")
                        .responseCode(403)
                        .build();
            }

            // ✅ Fetch media metadata for all contents in this folder
            List<Optional<MediaMetadata>> mediaMetadataList = folder.getContents().stream()
                    .map(mediaMetadataRepository::findByContent)
                    .filter(metadata -> metadata != null)
                    .collect(Collectors.toList());

            // ✅ Build response
            FolderResponse folderResponse = FolderResponse.builder()
                    .folderId(folder.getFolderId())
                    .name(folder.getName())
                    .portalId(folder.getPortalId())
                    .description(folder.getDescription())
                    .isUniversal(folder.getIsUniversal())
                    .price(folder.getPrice())
                    .accessDurationInDays(folder.getAccessDurationInDays())
                    .isRoot(folder.getIsRoot())
                    .adminUserIds(
                            folder.getAdmins().stream()
                                    .map(admin -> admin.getUserId())
                                    .collect(Collectors.toList())
                    )
                    // Instead of content list, return media metadata list
                    .mediaMetadataList(mediaMetadataList)
                    .build();


            return ResponseDTO.builder()
                    .status("SUCCESS")
                    .message("Folder details retrieved successfully")
                    .data(folder) // returning entity directly
                    .responseCode(2000)
                    .build();

        } catch (Exception e) {
            return ResponseDTO.builder()
                    .status("FAILURE")
                    .message("Error fetching folder details: " + e.getMessage())
                    .build();
        }
    }



}
