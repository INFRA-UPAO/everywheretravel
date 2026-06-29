package com.everywhere.backend.repository;

import com.everywhere.backend.model.entity.Proveedor;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProveedorRepository extends JpaRepository<Proveedor, Integer> {
    boolean existsByRuc(Integer ruc);
}