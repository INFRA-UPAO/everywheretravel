package com.everywhere.backend.api;

import com.everywhere.backend.model.dto.NaturalJuridicoRequestDTO;
import com.everywhere.backend.model.dto.NaturalJuridicoResponseDTO;
import com.everywhere.backend.model.dto.NaturalJuridicoPatchDTO;
import com.everywhere.backend.security.RequirePermission;
import com.everywhere.backend.service.NaturalJuridicoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/natural-juridico")
@RequiredArgsConstructor
public class NaturalJuridicoController {

    private final NaturalJuridicoService naturalJuridicoService;

    @GetMapping
    @RequirePermission(module = "PERSONAS", permission = "READ")
    public ResponseEntity<List<NaturalJuridicoResponseDTO>> getAllRelaciones() {
        return ResponseEntity.ok(naturalJuridicoService.findAll());
    }

    @GetMapping("/{id}")
    @RequirePermission(module = "PERSONAS", permission = "READ")
    public ResponseEntity<NaturalJuridicoResponseDTO> getRelacionById(@PathVariable Integer id) { 
        return ResponseEntity.ok(naturalJuridicoService.findById(id));
    }

    @GetMapping("/persona-natural/{personaNaturalId}")
    @RequirePermission(module = "PERSONAS", permission = "READ")
    public ResponseEntity<List<NaturalJuridicoResponseDTO>> getRelacionesByPersonaNatural(
            @PathVariable Integer personaNaturalId) { 
        return ResponseEntity.ok(naturalJuridicoService.findByPersonaNaturalId(personaNaturalId));
    }

    @GetMapping("/persona-juridica/{personaJuridicaId}")
    @RequirePermission(module = "PERSONAS", permission = "READ")
    public ResponseEntity<List<NaturalJuridicoResponseDTO>> getRelacionesByPersonaJuridica(
            @PathVariable Integer personaJuridicaId) { 
        return ResponseEntity.ok(naturalJuridicoService.findByPersonaJuridicaId(personaJuridicaId));
    }

    @PostMapping
    @RequirePermission(module = "PERSONAS", permission = "CREATE")
    public ResponseEntity<List<NaturalJuridicoResponseDTO>> crearRelaciones(
            @Valid @RequestBody NaturalJuridicoRequestDTO naturalJuridicoRequestDTO) {
        return ResponseEntity.status(HttpStatus.CREATED).body(naturalJuridicoService.crearRelaciones(naturalJuridicoRequestDTO));
    }

    @DeleteMapping("/{id}")
    @RequirePermission(module = "PERSONAS", permission = "DELETE")
    public ResponseEntity<Void> deleteRelacion(@PathVariable Integer id) {
        naturalJuridicoService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/persona-natural/{personaNaturalId}/persona-juridica/{personaJuridicaId}")
    @RequirePermission(module = "PERSONAS", permission = "DELETE")
    public ResponseEntity<Void> deleteRelacionByPersonas(
            @PathVariable Integer personaNaturalId,
            @PathVariable Integer personaJuridicaId) {
        naturalJuridicoService.deleteByPersonas(personaNaturalId, personaJuridicaId);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/persona-natural/{personaNaturalId}")
    @RequirePermission(module = "PERSONAS", permission = "UPDATE")
    public ResponseEntity<List<NaturalJuridicoResponseDTO>> patchRelacionesPersonaNatural(
            @PathVariable Integer personaNaturalId, @RequestBody NaturalJuridicoPatchDTO naturalJuridicoPatchDTO) { 
        return ResponseEntity.ok(naturalJuridicoService.patchRelacionesPersonaNatural(personaNaturalId, naturalJuridicoPatchDTO));
    }
}