package com.apex_aura.content_manager.dto;

import lombok.*;

import java.io.Serializable;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class ResponseDTO implements Serializable {
    private static final long serialVersionUID = 1L;
    private String status;
    private String message;
    private Integer responseCode;
    private transient Object data;
}

