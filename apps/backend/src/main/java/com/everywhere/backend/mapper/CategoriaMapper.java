package com.everywhere.backend.mapper;

import com.everywhere.backend.model.entity.Categoria;

import lombok.RequiredArgsConstructor;

import com.everywhere.backend.model.dto.CategoriaRequestDto;
import com.everywhere.backend.model.dto.CategoriaResponseDto;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CategoriaMapper {

	private final ModelMapper modelMapper;

	public Categoria toEntity(CategoriaRequestDto categoriaRequestDto) {
		return modelMapper.map(categoriaRequestDto, Categoria.class);
	}

	public CategoriaResponseDto toResponseDto(Categoria categoria) {
		return modelMapper.map(categoria, CategoriaResponseDto.class);
	}

	public void updateEntityFromDTO(CategoriaRequestDto categoriaRequestDto, Categoria categoria) {
		modelMapper.map(categoriaRequestDto, categoria);
	}
}