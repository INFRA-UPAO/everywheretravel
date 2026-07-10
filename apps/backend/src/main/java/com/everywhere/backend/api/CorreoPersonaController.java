package com.everywhere.backend.api;

import com.everywhere.backend.model.dto.CorreoPersonaRequestDTO;
import com.everywhere.backend.model.dto.CorreoPersonaResponseDTO;
import com.everywhere.backend.security.RequirePermission;
import com.everywhere.backend.service.CorreoPersonaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/correos-persona")
@RequiredArgsConstructor
public class CorreoPersonaController {

    private final CorreoPersonaService correoPersonaService;

    @GetMapping("/personas/{personaId}")
    @RequirePermission(module = "PERSONAS", permission = "READ")
    public ResponseEntity<List<CorreoPersonaResponseDTO>> findByPersonaId(@PathVariable Integer personaId) {
        return ResponseEntity.ok(correoPersonaService.findByPersonaId(personaId));
    }

    @GetMapping("/{correoId}")
    @RequirePermission(module = "PERSONAS", permission = "READ")
    public ResponseEntity<CorreoPersonaResponseDTO> findById(@PathVariable Integer correoId) {
        return correoPersonaService.findById(correoId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/personas/{personaId}")
    @RequirePermission(module = "PERSONAS", permission = "CREATE")
    public ResponseEntity<CorreoPersonaResponseDTO> save(
            @PathVariable Integer personaId,
            @RequestBody CorreoPersonaRequestDTO correoPersonaRequestDTO) {
        return ResponseEntity.ok(correoPersonaService.save(correoPersonaRequestDTO, personaId));
    }

    @PatchMapping("/personas/{personaId}/correo/{correoId}")
    @RequirePermission(module = "PERSONAS", permission = "UPDATE")
    public ResponseEntity<CorreoPersonaResponseDTO> update(
            @PathVariable Integer personaId,
            @PathVariable Integer correoId,
            @RequestBody CorreoPersonaRequestDTO correoPersonaRequestDTO) {
        return ResponseEntity.ok(correoPersonaService.update(personaId, correoPersonaRequestDTO, correoId));
    }

    @DeleteMapping("/{correoId}")
    @RequirePermission(module = "PERSONAS", permission = "DELETE")
    public ResponseEntity<Void> deleteById(@PathVariable Integer correoId) {
        correoPersonaService.deleteById(correoId);
        return ResponseEntity.noContent().build();
    }
}
