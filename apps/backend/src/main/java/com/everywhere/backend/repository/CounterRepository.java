package com.everywhere.backend.repository;

import com.everywhere.backend.model.entity.Counter;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CounterRepository extends JpaRepository<Counter, Integer> {
    Optional<Counter> findByCodigo(String codigo);
    Optional<Counter> findByNombreIgnoreCase(String nombre);
}