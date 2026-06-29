package com.everywhere.backend.model.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "correos_personas")
public class CorreoPersona {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "corre_id_int")
    private Integer id;

    @Column(name = "corre_emai_vac", length = 100, nullable = false)
    private String email;

    @Column(name = "corre_tipo_vac", length = 20)
    private String tipo;

    @CreationTimestamp
    @Column(name = "corre_cre_tmp", updatable = false)
    private LocalDateTime creado;

    @UpdateTimestamp
    @Column(name = "corre_upd_tmp")
    private LocalDateTime actualizado;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "per_id_int")
    @JsonBackReference
    private Personas persona;
}
