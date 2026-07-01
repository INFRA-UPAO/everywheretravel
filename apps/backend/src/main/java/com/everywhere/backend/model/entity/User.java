package com.everywhere.backend.model.entity;

import java.time.LocalDateTime;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

@Data
@Entity
@Table(name = "usuarios")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "usr_id_int")
    private Integer id;

    @Column(name = "rol_nam_vc", length = 100)
    private String nombre;

    @Column(name="usr_email_vc", length = 150)
    private String email;

    @Column(name = "usr_pass_vc", length = 255)
    private String password;

    @CreationTimestamp
    @Column(name = "prov_cre_tmp", updatable = false)
    private LocalDateTime creado;

    @UpdateTimestamp
    @Column(name = "prov_upd_tmp")
    private LocalDateTime actualizado;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "rol_id", nullable = false)
    private Role role;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "suc_id_int", nullable = true)
    private Sucursal sucursal;

}
