package com.everywhere.backend.model.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

@Data
@Entity
@Table(name = "natural_juridico")
public class NaturalJuridico {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "nat_jur_id_int")
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "per_nat_id_int", nullable = false)
    private PersonaNatural personaNatural;

    @ManyToOne
    @JoinColumn(name = "per_jur_id_int", nullable = false)
    private PersonaJuridica personaJuridica;

    @CreationTimestamp
    @Column(name = "nat_jur_cre_tmp", updatable = false)
    private LocalDateTime fechaCreacion;

    @UpdateTimestamp
    @Column(name = "nat_jur_upd_tmp")
    private LocalDateTime fechaActualizacion;
}
