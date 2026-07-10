package com.everywhere.backend.model.entity;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

@Data
@Entity
@Table(name = "proveedor_colaborador")
public class ProveedorColaborador {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "prov_col_id_int")
    private Integer id;

    @Column(name = "prov_col_cargo_int", length = 100)
    private String cargo;

    @Column(name = "prov_col_nom_vac", length = 150)
    private String nombre;

    @Column(name = "prov_col_email_int", length = 150)
    private String email;

    @Column(name = "prov_col_telf_int", length = 50)
    private String telefono;

    @Column(name = "prov_col_cod_pais_vac", length = 10)
    private String codigoPais;

    @Column(name = "pro_col_detalle_vac", length = 500)
    private String detalle;

    @CreationTimestamp
    @Column(name = "prov_col_cre_tmp", updatable = false)
    private LocalDateTime creado;

    @UpdateTimestamp
    @Column(name = "prov_col_upd_tmp")
    private LocalDateTime actualizado;

    @ManyToOne
    @JoinColumn(name = "prov_id_int")
    @JsonIgnoreProperties({ "contactos", "colaboradores" })
    private Proveedor proveedor;
}
