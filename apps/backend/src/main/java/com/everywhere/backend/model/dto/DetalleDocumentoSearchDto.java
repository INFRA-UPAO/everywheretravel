package com.everywhere.backend.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DetalleDocumentoSearchDto {
    
    // De DetalleDocumento
    private String numero;
    
    // De Personas (persona vinculada a PersonaNatural)
    private Integer personasId;
    
    // De PersonaNatural
    private String nombres;
    private String apellidosPaterno;
    private String apellidosMaterno;
    private String sexo;
}
