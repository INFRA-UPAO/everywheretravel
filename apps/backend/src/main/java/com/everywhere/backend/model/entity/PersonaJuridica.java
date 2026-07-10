package com.everywhere.backend.model.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

@Data
@Entity
@Table(name = "persona_juridica")
public class PersonaJuridica {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "per_jurd_id_int")
    private Integer id;

    @Column(name = "per_jurd_ruc_int")
    private String ruc;

    @Column(name = "per_jurd_razSocial_vac")
    private String razonSocial;

    @CreationTimestamp
    @Column(name = "per_jurd_cre_tmp", updatable = false)
    private LocalDateTime creado;

    @UpdateTimestamp
    @Column(name = "per_jurd_upd_tmp")
    private LocalDateTime actualizado;

    @ManyToOne
    @JoinColumn(name = "per_id_int", nullable = false)
    private Personas personas;
}
