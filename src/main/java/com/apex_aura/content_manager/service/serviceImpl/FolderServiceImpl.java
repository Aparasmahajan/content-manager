package com.apex_aura.content_manager.service.serviceImpl;

import com.apex_aura.content_manager.dto.AdminDto;
import com.apex_aura.content_manager.dto.PortalDto;
import com.apex_aura.content_manager.dto.ResponseDTO;
import com.apex_aura.content_manager.dto.request.FolderRequest;
import com.apex_aura.content_manager.dto.response.FolderResponse;
import com.apex_aura.content_manager.entity.Folder;
import com.apex_aura.content_manager.entity.FolderAdmin;
import com.apex_aura.content_manager.repository.FolderRepository;
import com.apex_aura.content_manager.repository.FolderAdminRepository;
import com.apex_aura.content_manager.service.FolderService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class FolderServiceImpl implements FolderService {
    private final FolderRepository folderRepository;
    private final FolderAdminRepository folderAdminRepository;
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

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
    public ResponseDTO getFolderDetails(Long folderId) {
        try {
            Folder folder = folderRepository.findById(folderId).orElse(null);
            if (folder == null) {
                return ResponseDTO.builder()
                        .status("FAILURE")
                        .message("Folder not found")
                        .responseCode(5001)
                        .build();
            }

            // Eagerly load admins, contents, and subfolders (optional, depends on JPA fetch type)
            folder.getAdmins().size();     // initialize admins
            folder.getContents().size();   // initialize contents
            folder.getSubFolders().forEach(sub -> {
                sub.getAdmins().size();
                sub.getContents().size();
                // recursively initialize deeper subfolders if needed
            });

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
