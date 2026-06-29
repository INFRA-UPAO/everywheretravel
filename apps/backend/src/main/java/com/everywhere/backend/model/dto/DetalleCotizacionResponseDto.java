package com.everywhere.backend.model.dto;

import com.everywhere.backend.model.entity.Categoria;
import com.everywhere.backend.model.entity.Cotizacion;
import com.everywhere.backend.model.entity.Operador;
import com.everywhere.backend.model.entity.Producto;
import com.everywhere.backend.model.entity.Proveedor;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class DetalleCotizacionResponseDto {
    private int id;
    private Integer cantidad;
    private Integer unidad;
    private String descripcion;
    private BigDecimal precioHistorico;
    private Boolean seleccionado;
    private LocalDateTime creado;
    private LocalDateTime actualizado;
    private BigDecimal comision;
    private Categoria categoria;
    private Cotizacion cotizacion;
    private Producto producto;
    private Proveedor proveedor;
    private Operador operador;
}