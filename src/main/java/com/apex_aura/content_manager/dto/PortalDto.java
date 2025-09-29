package com.apex_aura.content_manager.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
public class PortalDto {
    private Long portalId;
    private String portalName;
    private List<AdminDto> admins;
    private Boolean isActive;
}

