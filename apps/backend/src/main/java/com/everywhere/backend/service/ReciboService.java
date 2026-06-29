package com.everywhere.backend.service;

import java.io.ByteArrayInputStream;
import java.util.List;

import com.everywhere.backend.model.dto.ReciboResponseDTO;
import com.everywhere.backend.model.dto.ReciboUpdateDTO;

public interface ReciboService {
    ReciboResponseDTO createRecibo(Integer cotizacionId, Integer personaJuridicaId, Integer sucursalId);
    ByteArrayInputStream generatePdf(Integer reciboId);
    ReciboResponseDTO findById(Integer id);
    ReciboResponseDTO findBySerieAndCorrelativo(String serie, Integer correlativo);
    List<ReciboResponseDTO> findAll();
    ReciboResponseDTO findByCotizacionId(Integer cotizacionId);
    ReciboResponseDTO patchRecibo(Integer id, ReciboUpdateDTO reciboUpdateDTO);
    List<ReciboResponseDTO> findByCarpeta(Integer carpetaId);
    List<ReciboResponseDTO> findSinCarpeta();
    ReciboResponseDTO updateCarpeta(Integer id, Integer carpetaId);
}
