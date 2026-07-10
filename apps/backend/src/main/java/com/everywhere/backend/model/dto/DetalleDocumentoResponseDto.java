package com.everywhere.backend.model.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

import lombok.Data;

@Data
public class DetalleDocumentoResponseDto {
    private Integer id;
    private String numero;
    private LocalDate fechaEmision;  //yyyy-MM-dd
    private LocalDate fechaVencimiento;
    private String origen;
    private LocalDateTime creado;
    private LocalDateTime actualizado;
    private DocumentoResponseDto documento;
    private PersonaNaturalResponseDTO personaNatural;
}