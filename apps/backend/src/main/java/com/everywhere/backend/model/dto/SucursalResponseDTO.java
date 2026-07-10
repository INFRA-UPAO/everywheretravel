package com.everywhere.backend.model.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class SucursalResponseDTO {
    private Integer id;
    private String descripcion;
    private String direccion;
    private String telefono;
    private String email;
    private Boolean estado;
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaActualizacion;
}
