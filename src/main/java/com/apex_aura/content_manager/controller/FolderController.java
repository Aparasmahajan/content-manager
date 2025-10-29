package com.apex_aura.content_manager.controller;

import com.apex_aura.content_manager.dto.ResponseDTO;
import com.apex_aura.content_manager.dto.request.FolderAccessRequest;
import com.apex_aura.content_manager.dto.request.FolderAdminRequest;
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
    ResponseDTO createPortalInfo(@RequestBody FolderRequest folderRequest, HttpServletRequest request) {
        return folderService.createFolder(folderRequest, request);
    }


    @GetMapping("/getFolderDetails")
    public ResponseDTO getFolderDetails(@RequestParam Long folderId, HttpServletRequest request) {
        return folderService.getFolderDetails(folderId, request);
    }


    @GetMapping("/getPortalFolders")
    public ResponseDTO getFolderDetailsByPortalId(@RequestParam Long portalId, HttpServletRequest request) {
        return folderService.getFolderDetailsByPortalId(portalId, request);
    }


    @PostMapping("/folderAccessUpdate")
    public ResponseDTO folderAccessUpdate(@RequestBody FolderAccessRequest folderAccessRequest, HttpServletRequest request) {
        return folderService.folderAccessUpdate(folderAccessRequest, request);
    }


    @PostMapping("/getFolderAdmins")
    public ResponseDTO getFolderAdmins(@RequestBody FolderAdminRequest folderAdminRequest, HttpServletRequest request) {
        return folderService.getFolderAdmins(folderAdminRequest, request);
    }
}
