package com.everywhere.backend.model.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "counter")
public class Counter {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "cou_id_int")
    private int id;

    @Column(name = "cou_nom_vac", length = 150)
    private String nombre;

    @Column(name = "cou_est_bol")
    private Boolean estado;

    @Column (name = "cou_cod_vac", length = 50)
    private String codigo;

    @Column (name = "cou_fec_crea_tmp", updatable = false)
    private LocalDateTime fechaCreacion;

    @Column (name = "cou_fec_actu_tmp")
    private LocalDateTime fechaActualizacion;
}
