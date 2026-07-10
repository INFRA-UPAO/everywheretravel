package com.everywhere.backend.mapper;

import com.everywhere.backend.model.dto.FormaPagoRequestDTO;
import com.everywhere.backend.model.dto.FormaPagoResponseDTO;
import com.everywhere.backend.model.entity.FormaPago;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class FormaPagoMapper {

    @Autowired
    private ModelMapper modelMapper;

    public FormaPago toEntity(FormaPagoRequestDTO formaPagoRequestDTO) {
        return modelMapper.map(formaPagoRequestDTO, FormaPago.class);
    }

    public FormaPagoResponseDTO toResponseDTO(FormaPago formaPago) {
        return modelMapper.map(formaPago, FormaPagoResponseDTO.class);
    }
}