package com.everywhere.backend.repository;

import com.everywhere.backend.model.entity.Operador; 
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface OperadorRepository extends JpaRepository<Operador, Integer> {
    Optional<Operador> findByNombre(String nombre);
    List<Operador> nombre(String nombre);
    boolean existsByNombreIgnoreCase(String nombre);
}