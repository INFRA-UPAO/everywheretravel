package com.everywhere.backend.model.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class ProveedorContactoResponseDTO {
    private Integer id;
    private String descripcion;
    private String email;
    private String numero;
    private String codigoPais;
    private LocalDateTime creado;
    private LocalDateTime actualizado;
    private Integer proveedorId;
    private String proveedorNombre;
    private Integer grupoContactoId;
    private String grupoContactoNombre;
}
