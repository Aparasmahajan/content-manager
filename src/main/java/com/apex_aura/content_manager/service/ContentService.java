package com.apex_aura.content_manager.service;

import com.apex_aura.content_manager.dto.ResponseDTO;
import com.apex_aura.content_manager.dto.request.ContentRequest;
import com.apex_aura.content_manager.dto.request.FolderRequest;

public interface ContentService {
    ResponseDTO addContent(ContentRequest req);

    ResponseDTO getContentById(Long contentId);
}
