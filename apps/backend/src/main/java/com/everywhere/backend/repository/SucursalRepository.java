package com.everywhere.backend.repository;

import com.everywhere.backend.model.entity.Sucursal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SucursalRepository extends JpaRepository<Sucursal, Integer> {
    List<Sucursal> findByDescripcionContainingIgnoreCase(String descripcion);
    Optional<Sucursal> findByDescripcionIgnoreCase(String descripcion);
    List<Sucursal> findByEstado(Boolean estado);
    List<Sucursal> findByEstadoAndDescripcionContainingIgnoreCase(Boolean estado, String descripcion);
    List<Sucursal> findByDireccionContainingIgnoreCase(String direccion);
    Optional<Sucursal> findByEmail(String email);
    boolean existsByEmail(String email);

    boolean existsByDescripcion(String descripcion);
}
