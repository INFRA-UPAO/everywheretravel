package com.everywhere.backend.model.dto;
 
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties; 
import lombok.Data;
 
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class LiquidacionResponseDTO {
    private Integer id;
    private String numero;
    private LocalDate fechaCompra; 
    private String destino;
    private Integer numeroPasajeros; 
    private LocalDateTime creado;
    private LocalDateTime actualizado;

    private CotizacionResponseDto cotizacion;
    private ProductoResponseDTO producto;
    private FormaPagoResponseDTO formaPago; 
    private CarpetaResponseDto carpeta;
    @JsonIgnore
    private List<ObservacionLiquidacionResponseDTO> observacionesLiquidacion;

}