package com.everywhere.backend.model.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.type.SqlTypes;

@Data
@Entity
@Table(name = "personas")
public class Personas {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "per_id_int")
    private Integer id;

    @Column(name = "per_email_vac")
    private String email;

    @Column(name = "per_direc_vac")
    private String direccion;

    @Column(name = "per_obs_vac")
    @JdbcTypeCode(SqlTypes.LONGNVARCHAR)
    private String observacion; 

    @CreationTimestamp
    @Column(name = "per_cre_tmp", updatable = false)
    private LocalDateTime creado;

    @UpdateTimestamp
    @Column(name = "per_upd_tmp")
    private LocalDateTime actualizado;

    @OneToMany(mappedBy = "persona", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JsonManagedReference
    private List<TelefonoPersona> telefonos;

    @OneToMany(mappedBy = "persona", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JsonManagedReference
    private List<CorreoPersona> correos;


}