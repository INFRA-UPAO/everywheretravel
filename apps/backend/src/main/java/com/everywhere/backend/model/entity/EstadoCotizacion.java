package com.everywhere.backend.model.entity;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "estados_cotizacion")
public class EstadoCotizacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "est_cot_id_int")
    private int id;

    @Column(name = "est_cot_desc_vac", length = 200)
    private String descripcion;

    @CreationTimestamp
    @Column(name = "est_cot_cre_tmp", updatable = false)
    private LocalDateTime fechaCreacion;

    @UpdateTimestamp
    @Column(name = "est_cot_upd_tmp")
    private LocalDateTime fechaActualizacion;

}