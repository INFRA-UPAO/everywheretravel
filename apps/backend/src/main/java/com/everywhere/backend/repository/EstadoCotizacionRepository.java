package com.everywhere.backend.repository;

import com.everywhere.backend.model.entity.EstadoCotizacion;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EstadoCotizacionRepository extends JpaRepository<EstadoCotizacion,Integer> {
}