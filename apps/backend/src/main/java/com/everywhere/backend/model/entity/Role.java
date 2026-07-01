package com.everywhere.backend.model.entity;

import java.time.LocalDateTime;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

@Data
@Entity
@Table(name = "role")
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "rol_id")
    private Integer id;

    @Column(name = "rol_nam_vc", length = 100)
    private String name;

    @CreationTimestamp
    @Column(name = "prov_cre_tmp", updatable = false)
    private LocalDateTime creado;

    @UpdateTimestamp
    @Column(name = "prov_upd_tmp")
    private LocalDateTime actualizado;
}