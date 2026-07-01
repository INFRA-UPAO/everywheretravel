package com.everywhere.backend.api;

import com.everywhere.backend.model.dto.ViajeroConPersonaResponseDTO;
import com.everywhere.backend.model.dto.ViajeroRequestDTO;
import com.everywhere.backend.model.dto.ViajeroResponseDTO;
import com.everywhere.backend.security.RequirePermission;
import com.everywhere.backend.service.ViajeroService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/viajeros")
@RequiredArgsConstructor
public class ViajeroController {

    private final ViajeroService viajeroService;

    @GetMapping
    @RequirePermission(module = "VIAJEROS", permission = "READ")
    public ResponseEntity<List<ViajeroResponseDTO>> getAllViajeros() {
        return ResponseEntity.ok(viajeroService.findAll());
    }

    @GetMapping("/nacionalidad")
    @RequirePermission(module = "VIAJEROS", permission = "READ")
    public ResponseEntity<List<ViajeroResponseDTO>> getViajeroByNacionalidad(@RequestParam String nacionalidad) {
        return ResponseEntity.ok(viajeroService.findByNacionalidad(nacionalidad.trim()));
    }

    @GetMapping("/residencia")
    @RequirePermission(module = "VIAJEROS", permission = "READ")
    public ResponseEntity<List<ViajeroResponseDTO>> getViajeroByResidencia(@RequestParam String residencia) {
        return ResponseEntity.ok(viajeroService.findByResidencia(residencia.trim()));
    }

    @GetMapping("/{id}")
    @RequirePermission(module = "VIAJEROS", permission = "READ")
    public ResponseEntity<ViajeroResponseDTO> getViajeroById(@PathVariable Integer id) {
        return ResponseEntity.ok(viajeroService.findById(id));
    }

    @PostMapping
    @RequirePermission(module = "VIAJEROS", permission = "CREATE")
    public ResponseEntity<ViajeroResponseDTO> createViajero(@RequestBody ViajeroRequestDTO viajeroRequestDTO) {
        return ResponseEntity.status(HttpStatus.CREATED).body(viajeroService.save(viajeroRequestDTO));
    }

    @PatchMapping("/{id}")
    @RequirePermission(module = "VIAJEROS", permission = "UPDATE")
    public ResponseEntity<ViajeroResponseDTO> patch(@PathVariable Integer id, @RequestBody ViajeroRequestDTO viajeroRequestDTO) { 
        return ResponseEntity.ok(viajeroService.patch(id, viajeroRequestDTO));
    }

    @DeleteMapping("/{id}")
    @RequirePermission(module = "VIAJEROS", permission = "DELETE")
    public ResponseEntity<Void> deleteViajero(@PathVariable Integer id) {
        viajeroService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/con-persona-natural")
    @RequirePermission(module = "VIAJEROS", permission = "READ")
    public ResponseEntity<List<ViajeroConPersonaResponseDTO>> findAllWithPersonaNatural() {
        return ResponseEntity.ok(viajeroService.findAllWithPersonaNatural());
    }
}
