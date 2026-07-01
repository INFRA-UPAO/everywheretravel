package com.everywhere.backend.repository;

import com.everywhere.backend.model.entity.ProveedorColaborador;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProveedorColaboradorRepository extends JpaRepository<ProveedorColaborador, Integer> {

    List<ProveedorColaborador> findByProveedorId(Integer proveedorId);

    List<ProveedorColaborador> findByNombreContainingIgnoreCase(String nombre);

    Optional<ProveedorColaborador> findByEmail(String email);
}
