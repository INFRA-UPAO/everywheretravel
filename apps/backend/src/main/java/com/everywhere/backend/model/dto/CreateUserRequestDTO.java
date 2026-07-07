package com.everywhere.backend.model.dto;

import lombok.Data;

@Data
public class CreateUserRequestDTO {
    private String email;
    private String nombre;
    private Integer rolId;
    private Integer sucursalId;
}
