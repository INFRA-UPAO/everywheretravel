package com.everywhere.backend.repository;

import com.everywhere.backend.model.entity.ProveedorContacto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProveedorContactoRepository extends JpaRepository<ProveedorContacto, Integer> {

    List<ProveedorContacto> findByProveedorId(Integer proveedorId);

    List<ProveedorContacto> findByGrupoContactoId(Integer grupoContactoId);

    Optional<ProveedorContacto> findByEmail(String email);
}
