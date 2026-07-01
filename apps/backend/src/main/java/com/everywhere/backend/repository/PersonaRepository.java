package com.everywhere.backend.repository;

import com.everywhere.backend.model.entity.Personas;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PersonaRepository extends JpaRepository<Personas, Integer> {
    List<Personas> findByEmailContainingIgnoreCase(String email);
    @Query("""
       SELECT p 
       FROM Personas p 
       JOIN p.telefonos t 
       WHERE LOWER(t.numero) LIKE LOWER(CONCAT('%', :telefono, '%'))
       """)
    List<Personas> findByTelefonoContainingIgnoreCase(@Param("telefono") String telefono);

    @Query("SELECT p FROM Personas p LEFT JOIN FETCH p.telefonos WHERE p.id = :id")
    Optional<Personas> findByIdWithTelefonos(@Param("id") Integer id);
}