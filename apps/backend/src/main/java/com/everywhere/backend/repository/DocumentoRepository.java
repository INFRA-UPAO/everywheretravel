package com.everywhere.backend.repository;

import com.everywhere.backend.model.entity.Documento;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DocumentoRepository extends JpaRepository<Documento, Integer> {
}