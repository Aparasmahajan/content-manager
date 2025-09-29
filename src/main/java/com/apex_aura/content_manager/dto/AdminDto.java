package com.apex_aura.content_manager.dto;

import lombok.Data;

@Data
public class AdminDto {
    private Long userId;
    private String username;
    private String email;
    private Boolean isActive;
}
