package com.everywhere.backend.model.dto;

import lombok.Data;

@Data
public class ProveedorColaboradorRequestDTO {
    private String cargo;
    private String nombre;
    private String email;
    private String telefono;
    private String codigoPais;
    private String detalle;
    private Integer proveedorId;
}
