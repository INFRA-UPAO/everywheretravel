package com.everywhere.backend.model.dto;

import lombok.Data;

@Data
public class ProveedorContactoRequestDTO {
    private String descripcion;
    private String email;
    private String numero;
    private String codigoPais;
    private Integer proveedorId;
    private Integer grupoContactoId;
}
