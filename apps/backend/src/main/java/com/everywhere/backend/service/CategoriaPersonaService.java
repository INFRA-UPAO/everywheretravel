package com.everywhere.backend.service;

import com.everywhere.backend.model.dto.CategoriaPersonaRequestDTO;
import com.everywhere.backend.model.dto.CategoriaPersonaResponseDTO;
import com.everywhere.backend.model.dto.PersonaNaturalResponseDTO;

import java.util.List;

public interface CategoriaPersonaService {
    List<CategoriaPersonaResponseDTO> findAll();
    CategoriaPersonaResponseDTO findById(Integer id);
    List<CategoriaPersonaResponseDTO> findByNombre(String nombre);
    CategoriaPersonaResponseDTO save(CategoriaPersonaRequestDTO categoriaPersonaRequestDTO);
    CategoriaPersonaResponseDTO patch(Integer id, CategoriaPersonaRequestDTO categoriaPersonaRequestDTO);
    void deleteById(Integer id);

    PersonaNaturalResponseDTO asignarCategoria(Integer personaNaturalId, Integer categoriaId);
    PersonaNaturalResponseDTO desasignarCategoria(Integer personaNaturalId);
    List<PersonaNaturalResponseDTO> findPersonasPorCategoria(Integer categoriaId);
    CategoriaPersonaResponseDTO getCategoriaDePersona(Integer personaNaturalId);
}