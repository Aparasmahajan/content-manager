package com.apex_aura.content_manager.service;

import com.apex_aura.content_manager.dto.ResponseDTO;
import com.apex_aura.content_manager.dto.request.FolderRequest;
import jakarta.servlet.http.HttpServletRequest;

public interface FolderService {

    ResponseDTO createFolder(FolderRequest req);

    ResponseDTO getFolderDetails(Long folderId, HttpServletRequest request);
}
