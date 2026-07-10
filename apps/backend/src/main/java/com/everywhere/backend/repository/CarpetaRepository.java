package com.everywhere.backend.repository;

import com.everywhere.backend.model.entity.Carpeta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface CarpetaRepository extends JpaRepository<Carpeta, Integer> {

    List<Carpeta> findByCarpetaPadreId(Integer carpetaPadreId);
    List<Carpeta> findByNivel(Integer nivel);
    List<Carpeta> findByNombreContainingIgnoreCase(String nombre);
    List<Carpeta> findByCreadoBetween(LocalDateTime inicio, LocalDateTime fin);
    List<Carpeta> findByCreadoBetweenOrderByCreadoAsc(LocalDateTime inicio, LocalDateTime fin);
    List<Carpeta> findByCarpetaPadreIsNull(); // Carpeta raíz (sin padre) 
    List<Carpeta> findAllByOrderByCreadoDesc(); 

    @Query("SELECT COUNT(c) > 0 FROM Carpeta c WHERE c.nombre = :nombre AND c.nivel = :nivel")
    boolean existsByNombreAndNivel(@Param("nombre") String nombre, @Param("nivel") Integer nivel);

    @Query("SELECT c FROM Carpeta c WHERE YEAR(c.creado) = :anio AND MONTH(c.creado) = :mes")
    List<Carpeta> findByAnioAndMes(@Param("anio") int anio, @Param("mes") int mes);
}