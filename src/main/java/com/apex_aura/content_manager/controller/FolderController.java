package com.apex_aura.content_manager.controller;

import com.apex_aura.content_manager.dto.ResponseDTO;
import com.apex_aura.content_manager.dto.request.FolderRequest;
import com.apex_aura.content_manager.service.FolderService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.*;

@RestController
public class FolderController {
    private final FolderService folderService;

    public FolderController(FolderService folderService) {
        this.folderService = folderService;
    }

    @PostMapping("/createFolder")
    ResponseDTO createPortalInfo(@RequestBody FolderRequest folderRequest) {
        return folderService.createFolder(folderRequest);
    }


    @GetMapping("/getFolderDetails")
    public ResponseDTO getFolderDetails(@RequestParam Long folderId, HttpServletRequest request) {
        return folderService.getFolderDetails(folderId, request);
    }
}
