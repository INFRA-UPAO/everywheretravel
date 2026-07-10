package com.everywhere.backend.repository;

import com.everywhere.backend.model.entity.PersonaNatural;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PersonaNaturalRepository extends JpaRepository<PersonaNatural, Integer> {
    Optional<PersonaNatural> findByDocumentoIgnoreCase(String documento);
    Optional<PersonaNatural> findByPersonasId(Integer personaId);

    @Query(value = "SELECT * FROM persona_natural WHERE UPPER(TRANSLATE(per_nat_nomb_vac, 'ÁÉÍÓÚáéíóú', 'AEIOUaeiou')) LIKE UPPER(TRANSLATE(:nombres, 'ÁÉÍÓÚáéíóú', 'AEIOUaeiou'))", nativeQuery = true)
    List<PersonaNatural> findByNombresIgnoreAccents(@Param("nombres") String nombres);

    @Query(value = "SELECT * FROM persona_natural WHERE UPPER(TRANSLATE(per_nat_apell_pat_vac, 'ÁÉÍÓÚáéíóú', 'AEIOUaeiou')) LIKE UPPER(TRANSLATE(:apellidosPaterno, 'ÁÉÍÓÚáéíóú', 'AEIOUaeiou'))", nativeQuery = true)
    List<PersonaNatural> findByApellidosPaternoIgnoreAccents(@Param("apellidosPaterno") String apellidosPaterno);

    @Query(value = "SELECT * FROM persona_natural WHERE UPPER(TRANSLATE(per_nat_apell_mat_vac, 'ÁÉÍÓÚáéíóú', 'AEIOUaeiou')) LIKE UPPER(TRANSLATE(:apellidosMaterno, 'ÁÉÍÓÚáéíóú', 'AEIOUaeiou'))", nativeQuery = true)
    List<PersonaNatural> findByApellidosMaternoIgnoreAccents(@Param("apellidosMaterno") String apellidosMaterno);

    List<PersonaNatural> findByCategoriaPersonaId(Integer categoriaId);
    Optional<PersonaNatural> findByDocumentoIgnoreCaseAndIdNot(String documento, Integer id);
    @Query("SELECT COUNT(pn) FROM PersonaNatural pn WHERE pn.categoriaPersona.id = :categoriaId")
    long countByCategoriaPersonaId(@Param("categoriaId") Integer categoriaId);
}