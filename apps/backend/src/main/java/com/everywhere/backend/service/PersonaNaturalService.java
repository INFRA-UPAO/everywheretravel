package com.everywhere.backend.service;

import com.everywhere.backend.model.dto.PersonaNaturalRequestDTO;
import com.everywhere.backend.model.dto.PersonaNaturalResponseDTO;

import java.util.List; 

public interface PersonaNaturalService {
    List<PersonaNaturalResponseDTO> findAll();
    PersonaNaturalResponseDTO findById(Integer id);
    List<PersonaNaturalResponseDTO> findByDocumento(String documento);
    List<PersonaNaturalResponseDTO> findByNombres(String nombres);
    List<PersonaNaturalResponseDTO> findByApellidosPaternos(String apellidosPaternos);
    List<PersonaNaturalResponseDTO> findByApellidosMaternos(String apellidosMaternos);
    PersonaNaturalResponseDTO save(PersonaNaturalRequestDTO personaNaturalRequestDTO); 
    PersonaNaturalResponseDTO patch(Integer id, PersonaNaturalRequestDTO personaNaturalRequestDTO);
    void deleteById(Integer id);
    PersonaNaturalResponseDTO asociarViajero(Integer personaNaturalId, Integer viajeroId);
    PersonaNaturalResponseDTO desasociarViajero(Integer personaNaturalId);
}