package com.everywhere.backend.model.entity;

import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

@Data
@Entity
@Table(name = "proveedor_grupo_contacto")
public class ProveedorGrupoContacto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "prov_grup_id_int")
    private Integer id;

    @Column(name = "prov_grup_nom_vac", length = 150)
    private String nombre;

    @Column(name = "prov_grup_dec_vac", length = 300)
    private String descripcion;

    @CreationTimestamp
    @Column(name = "cat_cre_tmp", updatable = false)
    private LocalDateTime creado;

    @UpdateTimestamp
    @Column(name = "cat_upd_tmp")
    private LocalDateTime actualizado;

    @OneToMany(mappedBy = "grupoContacto", fetch = FetchType.LAZY)
    @JsonIgnoreProperties("grupoContacto")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private List<ProveedorContacto> contactos;
}
