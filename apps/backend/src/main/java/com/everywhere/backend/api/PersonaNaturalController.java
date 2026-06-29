package com.everywhere.backend.api;

import com.everywhere.backend.model.dto.PersonaNaturalRequestDTO;
import com.everywhere.backend.model.dto.PersonaNaturalResponseDTO;
import com.everywhere.backend.model.dto.PersonaNaturalViajeroDTO; 
import com.everywhere.backend.security.RequirePermission;
import com.everywhere.backend.service.PersonaNaturalService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/personas-naturales")
@RequiredArgsConstructor
public class PersonaNaturalController {

    private final PersonaNaturalService personaNaturalService;

    @GetMapping
    @RequirePermission(module = "PERSONAS", permission = "READ")
    public ResponseEntity<List<PersonaNaturalResponseDTO>> getAllPersonasNaturales() { 
        return ResponseEntity.ok(personaNaturalService.findAll());
    }

    @GetMapping("/documento")
    @RequirePermission(module = "PERSONAS", permission = "READ")
    public ResponseEntity<List<PersonaNaturalResponseDTO>> getPersonasNaturalesByDocumento(@RequestParam String documento) { 
        return ResponseEntity.ok(personaNaturalService.findByDocumento(documento.trim()));
    }

    @GetMapping("/nombres")
    @RequirePermission(module = "PERSONAS", permission = "READ")
    public ResponseEntity<List<PersonaNaturalResponseDTO>> getPersonasNaturalesByNombre(@RequestParam String nombres) { 
        return ResponseEntity.ok(personaNaturalService.findByNombres(nombres.trim()));
    }

    @GetMapping("/apellidos-paterno")
    @RequirePermission(module = "PERSONAS", permission = "READ")
    public ResponseEntity<List<PersonaNaturalResponseDTO>> getPersonasNaturalesByApellidoPaterno(@RequestParam String apellidos) { 
        return ResponseEntity.ok(personaNaturalService.findByApellidosPaternos(apellidos.trim()));
    } 

    @GetMapping("/apellidos-materno")
    @RequirePermission(module = "PERSONAS", permission = "READ")
    public ResponseEntity<List<PersonaNaturalResponseDTO>> getPersonasNaturalesByApellidoMaterno(@RequestParam String apellidos) { 
        return ResponseEntity.ok(personaNaturalService.findByApellidosMaternos(apellidos.trim()));
    } 

    @GetMapping("/{id}")
    @RequirePermission(module = "PERSONAS", permission = "READ")
    public ResponseEntity<PersonaNaturalResponseDTO> getPersonaNaturalById(@PathVariable Integer id) { 
        return ResponseEntity.ok(personaNaturalService.findById(id));
    }

    @PostMapping
    @RequirePermission(module = "PERSONAS", permission = "CREATE")
    public ResponseEntity<PersonaNaturalResponseDTO> createPersonaNatural(@Valid @RequestBody PersonaNaturalRequestDTO personaNaturalRequestDTO) { 
        return ResponseEntity.status(HttpStatus.CREATED).body(personaNaturalService.save(personaNaturalRequestDTO));
    }

    @PatchMapping("/{id}")
    @RequirePermission(module = "PERSONAS", permission = "UPDATE")
    public ResponseEntity<PersonaNaturalResponseDTO> patchPersonaNatural(@PathVariable Integer id, @RequestBody PersonaNaturalRequestDTO personaNaturalRequestDTO) { 
        return ResponseEntity.ok(personaNaturalService.patch(id, personaNaturalRequestDTO));
    }

    @PatchMapping("/{id}/asociar-viajero")
    @RequirePermission(module = "PERSONAS", permission = "UPDATE")
    public ResponseEntity<PersonaNaturalResponseDTO> patchAsociarViajero(@PathVariable Integer id, @RequestBody PersonaNaturalViajeroDTO personaNaturalViajeroDTO) { 
        return ResponseEntity.ok(personaNaturalService.asociarViajero(id, personaNaturalViajeroDTO.getViajeroId()));
    }

    @PatchMapping("/{id}/desasociar-viajero")
    @RequirePermission(module = "PERSONAS", permission = "UPDATE")
    public ResponseEntity<PersonaNaturalResponseDTO> desasociarViajero(@PathVariable Integer id) { 
        return ResponseEntity.ok(personaNaturalService.desasociarViajero(id));
    }

    @DeleteMapping("/{id}")
    @RequirePermission(module = "PERSONAS", permission = "DELETE")
    public ResponseEntity<Void> deletePersonaNatural(@PathVariable Integer id) {
        personaNaturalService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}