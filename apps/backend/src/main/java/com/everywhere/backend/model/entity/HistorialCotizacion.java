package com.everywhere.backend.model.entity;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;
import java.util.UUID;

@Data
@Entity
@Table(name = "historial_cotizaciones")
public class HistorialCotizacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "hist_cot_id_int")
    private Integer id;

    @Column(
        name = "hist_cot_uuid", 
        columnDefinition = "uuid DEFAULT gen_random_uuid() UNIQUE", 
        insertable = false, 
        updatable = false
    )
    private UUID uuid;

    @Column(name = "hist_cot_obs_vac", length = 500)
    private String observacion;

    @CreationTimestamp
    @Column(name = "hist_cot_cre_tmp", updatable = false)
    private LocalDateTime fechaCreacion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usr_id_int", referencedColumnName = "usr_id_int")
    private User usuario;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cot_id_int", referencedColumnName = "cot_id_int")
    private Cotizacion cotizacion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "est_cot_id_int", referencedColumnName = "est_cot_id_int")
    private EstadoCotizacion estadoCotizacion;
}
