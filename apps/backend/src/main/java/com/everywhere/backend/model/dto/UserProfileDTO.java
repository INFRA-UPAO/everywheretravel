package com.everywhere.backend.model.dto;

import lombok.Data;

@Data
public class UserProfileDTO {
    private Integer id;
    private String name;
    private String email;
    private String role;
    private SucursalResponseDTO sucursal;
}
