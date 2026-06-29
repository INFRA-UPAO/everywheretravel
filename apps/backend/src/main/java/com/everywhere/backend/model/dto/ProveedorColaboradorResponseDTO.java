package com.everywhere.backend.model.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class ProveedorColaboradorResponseDTO {
    private Integer id;
    private String cargo;
    private String nombre;
    private String email;
    private String telefono;
    private String codigoPais;
    private String detalle;
    private LocalDateTime creado;
    private LocalDateTime actualizado;
    private Integer proveedorId;
    private String proveedorNombre;
}
