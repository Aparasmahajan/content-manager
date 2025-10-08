package com.apex_aura.content_manager.service.serviceImpl;

import com.apex_aura.content_manager.dto.AdminDto;
import com.apex_aura.content_manager.dto.PortalDto;
import com.apex_aura.content_manager.dto.ResponseDTO;
import com.apex_aura.content_manager.dto.request.FolderAccessRequest;
import com.apex_aura.content_manager.dto.request.FolderRequest;
import com.apex_aura.content_manager.dto.response.FolderResponse;
import com.apex_aura.content_manager.entity.Folder;
import com.apex_aura.content_manager.entity.FolderAccess;
import com.apex_aura.content_manager.entity.FolderAdmin;
import com.apex_aura.content_manager.entity.MediaMetadata;
import com.apex_aura.content_manager.repository.FolderAccessRepository;
import com.apex_aura.content_manager.repository.FolderRepository;
import com.apex_aura.content_manager.repository.FolderAdminRepository;
import com.apex_aura.content_manager.repository.MediaMetadataRepository;
import com.apex_aura.content_manager.service.FolderService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.apache.catalina.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
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
    private final FolderAccessRepository folderAccessRepository;

    @Value("${profiler.portalInfo.url}")
    String portalInfoUrl;

    @Override
    public ResponseDTO createFolder(FolderRequest req, HttpServletRequest request) {
        try {
            Long userId = Long.valueOf(request.getHeader("userId"));
            req.setCreatedByUserId(userId);
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
                    .isRoot(req.getIsRoot() != null ? req.getIsRoot() : req.getParentFolderId() == null)
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
            Set<Long> folderAdminIds = folderAdminRepository.findAllByFolder_FolderId(folderId)
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
            List<MediaMetadata> mediaMetadataList = folder.getContents().stream()
                    .map(content -> mediaMetadataRepository.findByContent(content).orElse(null))
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());

            boolean isAdmin = folder.getAdmins().stream()
                    .filter(x-> x.getUserId().equals(requestingUserId))
                    .findAny()
                    .isPresent();

            // ✅ Build response
            FolderResponse folderResponse = mapToFolderResponse(folder, mediaMetadataList, isAdmin);


            return ResponseDTO.builder()
                    .status("SUCCESS")
                    .message("Folder details retrieved successfully")
                    .data(folderResponse) // returning entity directly
                    .responseCode(2000)
                    .build();

        } catch (Exception e) {
            return ResponseDTO.builder()
                    .status("FAILURE")
                    .message("Error fetching folder details: " + e.getMessage())
                    .build();
        }
    }

    private FolderResponse mapToFolderResponse(Folder folder, List<MediaMetadata> mediaList, Boolean canEdit) {
        return FolderResponse.builder()
                .folderId(folder.getFolderId())
                .portalId(folder.getPortalId())
                .name(folder.getName())
                .description(folder.getDescription())
                .isUniversal(folder.getIsUniversal())
                .price(folder.getPrice())
                .accessDurationInDays(folder.getAccessDurationInDays())
                .isRoot(folder.getIsRoot())
                .contents(folder.getContents().stream().toList())
                .admins(folder.getAdmins().stream().toList())
                .folderAccessList(folder.getFolderAccessList())
                .subFolders(folder.getSubFolders().stream()
                        .map(sub -> mapToFolderResponse(sub, mediaList, canEdit))
                        .collect(Collectors.toSet()))
                .mediaMetadataList(mediaList)
                .canEdit(canEdit)
                .createdAt(folder.getCreatedAt())
                .build();
    }
    @Override
    public ResponseDTO getFolderDetailsByPortalId(Long portalId, HttpServletRequest request) {
        try {
            Long requestingUserId = Long.parseLong(request.getHeader("userId"));

            // Fetch all folders under a portal
            List<Folder> folders = folderRepository.findByPortalId(portalId);
            if (folders == null || folders.isEmpty()) {
                return ResponseDTO.builder()
                        .status("FAILURE")
                        .message("No folders found for this portal")
                        .responseCode(5001)
                        .build();
            }

            // Build response for each folder
            List<FolderResponse> folderResponses = folders.stream().map(folder -> {

                // ✅ 1. Get folder admins
                Set<Long> folderAdminIds = folderAdminRepository.findAllByFolder_FolderId(folder.getFolderId())
                        .stream()
                        .map(FolderAdmin::getUserId)
                        .collect(Collectors.toSet());

                // ✅ 2. Check folder access table (paid access, expiry, etc.)
                boolean hasFolderAccess = folder.getFolderAccessList().stream().anyMatch(access ->
                        access.getUserId().equals(requestingUserId) &&
                                (access.getExpiresAt() == null || access.getExpiresAt().isAfter(LocalDateTime.now()))
                );

                // ✅ 3. Check final access: universal OR admin OR access granted
                boolean hasAccess = folder.getIsUniversal() || folderAdminIds.contains(requestingUserId) || hasFolderAccess;

                if (!hasAccess) {
                    // If user has no access — skip this folder
                    return null;
                }

                // ✅ 4. Fetch media metadata for all contents
                List<MediaMetadata> mediaMetadataList = folder.getContents().stream()
                        .map(content -> mediaMetadataRepository.findByContent(content).orElse(null))
                        .filter(metadata -> metadata != null)
                        .collect(Collectors.toList());

                // ✅ 5. Build response for this folder
                return FolderResponse.builder()
                        .folderId(folder.getFolderId())
                        .name(folder.getName())
                        .portalId(folder.getPortalId())
                        .description(folder.getDescription())
                        .isUniversal(folder.getIsUniversal())
                        .price(folder.getPrice())
                        .accessDurationInDays(folder.getAccessDurationInDays())
                        .isRoot(folder.getIsRoot())
                        .adminUserIds(folderAdminIds.stream().toList())
                        .mediaMetadataList(mediaMetadataList)
                        .canEdit(folderAdminIds.contains(requestingUserId)) // ✅ Add flag: can edit or not
                        .build();

            }).filter(folderResponse -> folderResponse != null).collect(Collectors.toList());

            List<FolderResponse> finalResponses = folderResponses.stream()
                    .filter(x-> x.getIsRoot())
                    .collect(Collectors.toList());

            return ResponseDTO.builder()
                    .status("SUCCESS")
                    .message("Folder details retrieved successfully")
                    .data(finalResponses)
                    .responseCode(2000)
                    .build();

        } catch (Exception e) {
            return ResponseDTO.builder()
                    .status("FAILURE")
                    .message("Error fetching folder details: " + e.getMessage())
                    .build();
        }
    }



    @Override
    public ResponseDTO folderAccessUpdate(FolderAccessRequest folderAccessRequest, HttpServletRequest request) {
        Folder folder = folderRepository.findById(folderAccessRequest.getFolderId()).orElse(null);
        if (folder == null) {
            return ResponseDTO.builder()
                    .status("FAILURE")
                    .message("Folder not found")
                    .responseCode(5001)
                    .build();
        }

        List<FolderAccess> folderAccessList = folderAccessRequest.getUserIds().stream()
                .map(userId -> FolderAccess.builder()
                        .folder(folder)
                        .userId(userId)
                        .grantedAt(LocalDateTime.now())
                        .expiresAt(folder.getAccessDurationInDays() != null
                                ? LocalDateTime.now().plusDays(folder.getAccessDurationInDays())
                                : LocalDateTime.now().plusYears(1))
                        .hasPaid(false)
                        .build())
                .toList();


        try {
            folderAccessRepository.saveAll(folderAccessList);
        } catch (Exception e) {
            throw new RuntimeException(e.getStackTrace().toString());
        }

        return ResponseDTO.builder()
                .status("SUCCESS")
                .message("Access granted to all users in portal " + folder.getPortalId())
                .responseCode(2000)
                .build();
    }
}
