package com.everywhere.backend.repository;

import com.everywhere.backend.model.entity.Producto;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductoRepository extends JpaRepository<Producto, Integer> {
    boolean existsProductosByTipo(String tipo);
}