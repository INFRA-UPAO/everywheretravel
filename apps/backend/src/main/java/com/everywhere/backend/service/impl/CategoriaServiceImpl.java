package com.everywhere.backend.service.impl;

import com.everywhere.backend.exceptions.ConflictException;
import com.everywhere.backend.model.dto.CategoriaRequestDto;
import com.everywhere.backend.model.dto.CategoriaResponseDto;
import com.everywhere.backend.model.entity.Categoria; 
import com.everywhere.backend.repository.CategoriaRepository;
import com.everywhere.backend.exceptions.ResourceNotFoundException;
import com.everywhere.backend.mapper.CategoriaMapper;
import com.everywhere.backend.repository.DetalleCotizacionRepository;
import com.everywhere.backend.service.CategoriaService;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import java.util.List; 

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CategoriaServiceImpl implements CategoriaService {

	private final CategoriaRepository categoriaRepository;
	private final CategoriaMapper categoriaMapper;
    private final DetalleCotizacionRepository detalleCotizacionRepository;

	@Override
	public List<CategoriaResponseDto> findAll() {
		return mapToResponseList(categoriaRepository.findAll());
	}

    private List<CategoriaResponseDto> mapToResponseList(List<Categoria> categorias) {
        return categorias.stream()
                .map(categoriaMapper::toResponseDto)
                .toList();
    }

    @Override
	public CategoriaResponseDto findById(int id) {
		Categoria categoria = categoriaRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Categoria no encontrada"));
		return categoriaMapper.toResponseDto(categoria);
	}

	@Override
	@Transactional
	public CategoriaResponseDto create(CategoriaRequestDto categoriaRequestDto) {
		if(categoriaRepository.existsByNombreIgnoreCase(categoriaRequestDto.getNombre()))
			throw new DataIntegrityViolationException("Ya existe una categoría con el nombre: " + categoriaRequestDto.getNombre());
		Categoria categoria = categoriaMapper.toEntity(categoriaRequestDto); 
		return categoriaMapper.toResponseDto(categoriaRepository.save(categoria));
	}

	@Override
	@Transactional
	public CategoriaResponseDto patch(int id, CategoriaRequestDto categoriaRequestDto) {
		if (!categoriaRepository.existsById(id))
			throw new ResourceNotFoundException("Categoria no encontrada con ID: " + id);

		if (categoriaRequestDto.getNombre() != null && 
			categoriaRepository.existsByNombreIgnoreCase(categoriaRequestDto.getNombre())) {
			Categoria categoria = categoriaRepository.findById(id).get();
			if (!categoriaRequestDto.getNombre().equalsIgnoreCase(categoria.getNombre()))
				throw new DataIntegrityViolationException("Ya existe una categoría con el nombre: " + categoriaRequestDto.getNombre());
		}

		categoriaMapper.updateEntityFromDTO(categoriaRequestDto, categoriaRepository.findById(id).get());
		return categoriaMapper.toResponseDto(categoriaRepository.save(categoriaRepository.findById(id).get()));
	}

    @Override
    @Transactional
    public void delete(int id) {
        if (!categoriaRepository.existsById(id))
            throw new ResourceNotFoundException("Categoria no encontrada con ID: " + id);

        long detallesCount = detalleCotizacionRepository.countByCategoriaId(id);
        if (detallesCount > 0) {
            throw new ConflictException(
                    "No se puede eliminar esta categoría porque tiene " + detallesCount + " detalle(s) de cotización asociado(s).",
                    "/api/v1/categorias/" + id
            );
        }

        categoriaRepository.deleteById(id);
    }
}