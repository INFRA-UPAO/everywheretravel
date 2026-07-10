package com.everywhere.backend.api;

import com.everywhere.backend.model.dto.TelefonoPersonaRequestDTO;
import com.everywhere.backend.model.dto.TelefonoPersonaResponseDTO;
import com.everywhere.backend.security.RequirePermission;
import com.everywhere.backend.service.TelefonoPersonaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/telefonos-persona")
@RequiredArgsConstructor
public class    TelefonoPersonaController {

    private final TelefonoPersonaService telefonoPersonaService;

    @GetMapping("/personas/{personaId}")
    @RequirePermission(module = "PERSONAS", permission = "READ")
    public ResponseEntity<List<TelefonoPersonaResponseDTO>> findByPersonaId(@PathVariable Integer personaId) {
        return ResponseEntity.ok(telefonoPersonaService.findByPersonaId(personaId));
    }

    @GetMapping("/personas/{personaId}/telefono/{telefonoId}")
    @RequirePermission(module = "PERSONAS", permission = "READ")
    public ResponseEntity<TelefonoPersonaResponseDTO> findById(@PathVariable Integer personaId, @PathVariable Integer telefonoId) {
        return telefonoPersonaService.findById(telefonoId, personaId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/personas/{personaId}")
    @RequirePermission(module = "PERSONAS", permission = "CREATE")
    public ResponseEntity<TelefonoPersonaResponseDTO> create(@PathVariable Integer personaId,
                                                             @RequestBody @Valid TelefonoPersonaRequestDTO telefonoPersonaRequestDTO) {
        return new ResponseEntity<>(telefonoPersonaService.save(telefonoPersonaRequestDTO, personaId), HttpStatus.CREATED);
    }

    @PatchMapping("/personas/{personaId}/telefono/{telefonoId}")
    @RequirePermission(module = "PERSONAS", permission = "UPDATE")
    public ResponseEntity<TelefonoPersonaResponseDTO> update(@PathVariable Integer personaId,
                                                             @PathVariable Integer telefonoId,
                                                             @RequestBody  TelefonoPersonaRequestDTO telefonoPersonaRequestDTO) {
        return ResponseEntity.ok(telefonoPersonaService.update(personaId, telefonoPersonaRequestDTO, telefonoId));
    }

    @DeleteMapping("/personas/{personaId}/telefono/{telefonoId}")
    @RequirePermission(module = "PERSONAS", permission = "DELETE")
    public ResponseEntity<Void> delete(@PathVariable Integer personaId, @PathVariable Integer telefonoId) {
        telefonoPersonaService.deleteById(telefonoId, personaId);
        return ResponseEntity.noContent().build();
    }
}
