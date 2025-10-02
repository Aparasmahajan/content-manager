package com.apex_aura.content_manager.controller;

import com.apex_aura.content_manager.dto.ResponseDTO;
import com.apex_aura.content_manager.dto.request.ContentRequest;
import com.apex_aura.content_manager.dto.request.FolderRequest;
import com.apex_aura.content_manager.service.ContentService;
import com.apex_aura.content_manager.service.FolderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
public class ContentController {
    private final ContentService contentService;
    @Autowired
    public ContentController(ContentService contentService) {
        this.contentService = contentService;
    }

    @PostMapping("/createContent")
    ResponseDTO createPortalInfo(@RequestBody ContentRequest contentRequest) {
        return contentService.addContent(contentRequest);
    }


    @GetMapping("/getContentById")
    public ResponseDTO getFolderDetails(@RequestParam Long contentId) {
        return contentService.getContentById(contentId);
    }
}
