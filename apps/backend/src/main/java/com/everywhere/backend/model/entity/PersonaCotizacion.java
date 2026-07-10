package com.everywhere.backend.model.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "personas_cotizaciones")
@Data
@IdClass(PersonaCotizacionId.class)
public class PersonaCotizacion {

    @Id
    @ManyToOne
    @JoinColumn(name = "per_id_int", nullable = false)
    private Personas personas;

    @Id
    @ManyToOne
    @JoinColumn(name = "cot_id_int", nullable = false)
    private Cotizacion cotizacion;
}