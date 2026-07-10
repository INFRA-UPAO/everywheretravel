package com.everywhere.backend.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DetalleDocumentoConPersonasDto {
    
    private String numeroDocumento;
    private String tipoDocumento;
    private List<PersonaInfo> personas;
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class PersonaInfo {
        private Integer personaId;
        private String nombreCompleto;
    }
}
