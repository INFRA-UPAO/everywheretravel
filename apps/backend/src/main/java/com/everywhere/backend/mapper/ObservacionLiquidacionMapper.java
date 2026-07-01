package com.everywhere.backend.mapper;

import com.everywhere.backend.model.dto.ObservacionLiquidacionRequestDTO;
import com.everywhere.backend.model.dto.ObservacionLiquidacionResponseDTO;
import com.everywhere.backend.model.entity.ObservacionLiquidacion; 
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ObservacionLiquidacionMapper {

    private final ModelMapper modelMapper; 

    @PostConstruct
    public void configureMapping() {
        modelMapper.typeMap(ObservacionLiquidacionRequestDTO.class, ObservacionLiquidacion.class)
                .addMappings(mapper -> mapper.skip(ObservacionLiquidacion::setLiquidacion));
    }

    public ObservacionLiquidacion toEntity(ObservacionLiquidacionRequestDTO observacionLiquidacionRequestDTO) {
        ObservacionLiquidacion observacionLiquidacion =
                modelMapper.map(observacionLiquidacionRequestDTO, ObservacionLiquidacion.class);

        return observacionLiquidacion;
    }

    public ObservacionLiquidacionResponseDTO toResponseDTO(ObservacionLiquidacion observacionLiquidacion) {
        ObservacionLiquidacionResponseDTO observacionLiquidacionResponseDTO =
                modelMapper.map(observacionLiquidacion, ObservacionLiquidacionResponseDTO.class);

        return observacionLiquidacionResponseDTO;
    }

    public void updateEntityFromDTO(ObservacionLiquidacionRequestDTO observacionLiquidacionRequestDTO,
                                    ObservacionLiquidacion observacionLiquidacion) {
        modelMapper.map(observacionLiquidacionRequestDTO, observacionLiquidacion);
   }
}