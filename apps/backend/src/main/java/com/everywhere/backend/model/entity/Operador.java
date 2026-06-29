package com.everywhere.backend.model.entity;

import java.time.LocalDateTime;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

@Data
@Entity
@Table(name = "operador")
public class Operador {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "opr_id_int")
    private int id;

    @Column(name = "opr_nomb_int", length = 150)
    private String nombre;

    @CreationTimestamp
    @Column(name = "opr_cre_tmp", updatable = false)
    private LocalDateTime creado;

    @UpdateTimestamp
    @Column(name = "opr_upd_tmp")
    private LocalDateTime actualizado;
}
