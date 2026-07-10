package com.everywhere.backend.model.dto;

import lombok.Data;

import java.util.Map;
import java.util.Set;

@Data
public class AuthResponseDTO {
    private Integer id;
    private String token;
    private String name;
    private String role;
    private Map<String, Set<String>> permissions;
}
