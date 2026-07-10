package com.everywhere.backend.service.impl;

import com.everywhere.backend.exceptions.ConflictException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import com.everywhere.backend.service.CategoriaPersonaService;
import com.everywhere.backend.repository.CategoriaPersonaRepository;
import com.everywhere.backend.repository.PersonaNaturalRepository;
import com.everywhere.backend.mapper.CategoriaPersonaMapper;
import com.everywhere.backend.mapper.PersonaNaturalMapper;
import com.everywhere.backend.model.dto.CategoriaPersonaRequestDTO;
import com.everywhere.backend.model.dto.CategoriaPersonaResponseDTO;
import com.everywhere.backend.model.dto.PersonaNaturalResponseDTO;
import com.everywhere.backend.model.entity.CategoriaPersona;
import com.everywhere.backend.model.entity.PersonaNatural;
import com.everywhere.backend.exceptions.ResourceNotFoundException;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CategoriaPersonaServiceImpl implements CategoriaPersonaService {
    
    private final CategoriaPersonaRepository categoriaPersonaRepository;
    private final CategoriaPersonaMapper categoriaPersonaMapper;
    private final PersonaNaturalRepository personaNaturalRepository;
    private final PersonaNaturalMapper personaNaturalMapper;

    @Override
    public List<CategoriaPersonaResponseDTO> findAll() {
        return mapToResponseList(categoriaPersonaRepository.findAll());
    }

    @Override
    public CategoriaPersonaResponseDTO findById(Integer id) {
        CategoriaPersona categoria = categoriaPersonaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Categoría de persona no encontrada con ID: " + id));
        return categoriaPersonaMapper.toResponseDTO(categoria);
    }

    @Override
    public List<CategoriaPersonaResponseDTO> findByNombre(String nombre) { 
        return mapToResponseList(categoriaPersonaRepository.findByNombreContainingIgnoreCase(nombre));
    }

    @Override
    @Transactional
    public CategoriaPersonaResponseDTO save(CategoriaPersonaRequestDTO categoriaPersonaRequestDTO) {
        if (categoriaPersonaRepository.existsByNombreIgnoreCase(categoriaPersonaRequestDTO.getNombre()))
            throw new DataIntegrityViolationException("Ya existe una categoría con el nombre: " + categoriaPersonaRequestDTO.getNombre());
        CategoriaPersona categoria = categoriaPersonaMapper.toEntity(categoriaPersonaRequestDTO); 
        return categoriaPersonaMapper.toResponseDTO(categoriaPersonaRepository.save(categoria));
    }

    @Override
    @Transactional
    public CategoriaPersonaResponseDTO patch(Integer id, CategoriaPersonaRequestDTO categoriaPersonaRequestDTO) {
        if (!categoriaPersonaRepository.existsById(id))
            throw new ResourceNotFoundException("Categoría de persona no encontrada con ID: " + id);
        
        if (categoriaPersonaRequestDTO.getNombre() != null && 
            categoriaPersonaRepository.existsByNombreIgnoreCase(categoriaPersonaRequestDTO.getNombre())) {
            CategoriaPersona existing = categoriaPersonaRepository.findById(id).get();
            if (!categoriaPersonaRequestDTO.getNombre().equalsIgnoreCase(existing.getNombre())) 
                throw new DataIntegrityViolationException("Ya existe una categoría con el nombre: " + categoriaPersonaRequestDTO.getNombre());
        }
        
        CategoriaPersona categoriaPersona = categoriaPersonaRepository.findById(id).get();
        categoriaPersonaMapper.updateEntityFromDTO(categoriaPersonaRequestDTO, categoriaPersona); 
        return categoriaPersonaMapper.toResponseDTO(categoriaPersonaRepository.save(categoriaPersona));
    }

    @Override
    @Transactional
    public void deleteById(Integer id) {
        if (!categoriaPersonaRepository.existsById(id))
            throw new ResourceNotFoundException("Categoría de persona no encontrada con ID: " + id);

        long personasNaturalesCount = personaNaturalRepository.countByCategoriaPersonaId(id);
        if (personasNaturalesCount > 0) {
            throw new ConflictException(
                    "No se puede eliminar esta categoría porque tiene " + personasNaturalesCount + " persona(s) natural(es) asociada(s).",
                    "/api/v1/categorias-persona/" + id
            );
        }

        categoriaPersonaRepository.deleteById(id);
    }

    @Override
    @Transactional
    public PersonaNaturalResponseDTO asignarCategoria(Integer personaNaturalId, Integer categoriaId) {
        if (!personaNaturalRepository.existsById(personaNaturalId))
            throw new ResourceNotFoundException("Persona natural no encontrada con ID: " + personaNaturalId);
        
        if (!categoriaPersonaRepository.existsById(categoriaId))
            throw new ResourceNotFoundException("Categoría no encontrada con ID: " + categoriaId);
        
        PersonaNatural personaNatural = personaNaturalRepository.findById(personaNaturalId).get();
        CategoriaPersona categoria = categoriaPersonaRepository.findById(categoriaId).get();
        
        personaNatural.setCategoriaPersona(categoria); 
        return personaNaturalMapper.toResponseDTO(personaNaturalRepository.save(personaNatural));
    }

    @Override
    @Transactional
    public PersonaNaturalResponseDTO desasignarCategoria(Integer personaNaturalId) {
        if (!personaNaturalRepository.existsById(personaNaturalId))
            throw new ResourceNotFoundException("Persona natural no encontrada con ID: " + personaNaturalId);

        PersonaNatural personaNatural = personaNaturalRepository.findById(personaNaturalId).get();
        personaNatural.setCategoriaPersona(null);
        return personaNaturalMapper.toResponseDTO(personaNaturalRepository.save(personaNatural));
    }

    @Override
    public List<PersonaNaturalResponseDTO> findPersonasPorCategoria(Integer categoriaId) {
        if (!categoriaPersonaRepository.existsById(categoriaId))
            throw new ResourceNotFoundException("Categoría no encontrada con ID: " + categoriaId);

        List<PersonaNatural> personasNaturales = personaNaturalRepository.findByCategoriaPersonaId(categoriaId);
        return personasNaturales.stream().map(personaNaturalMapper::toResponseDTO).toList();
    }

    @Override
    public CategoriaPersonaResponseDTO getCategoriaDePersona(Integer personaNaturalId) { 
        if (!personaNaturalRepository.existsById(personaNaturalId))
            throw new ResourceNotFoundException("Persona natural no encontrada con ID: " + personaNaturalId);

        PersonaNatural personaNatural = personaNaturalRepository.findById(personaNaturalId).get();
        CategoriaPersona categoriaPersona = personaNatural.getCategoriaPersona();
        if (categoriaPersona == null)
            throw new ResourceNotFoundException("La persona natural con ID: " + personaNaturalId + " no tiene categoría asignada.");
            
        return categoriaPersonaMapper.toResponseDTO(categoriaPersona);
    }

    private List<CategoriaPersonaResponseDTO> mapToResponseList(List<CategoriaPersona> categorias) {
        return categorias.stream().map(categoriaPersonaMapper::toResponseDTO).toList();
    }
}