package com.everywhere.backend.api;

import com.everywhere.backend.model.dto.PersonaRequestDTO;
import com.everywhere.backend.model.dto.PersonaResponseDTO;
import com.everywhere.backend.model.dto.PersonaDisplayDto;
import com.everywhere.backend.security.RequirePermission;
import com.everywhere.backend.service.PersonaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import java.util.List;

@RestController
@RequestMapping("/personas")
@RequiredArgsConstructor
public class PersonaController {

    private final PersonaService personaService;

    @GetMapping
    @RequirePermission(module = "PERSONAS", permission = "READ")
    public ResponseEntity<List<PersonaResponseDTO>> getAllPersonas() { 
        return ResponseEntity.ok(personaService.findAll());
    }

    @GetMapping("/{id}")
    @RequirePermission(module = "PERSONAS", permission = "READ")
    public ResponseEntity<PersonaResponseDTO> getPersonaById(@PathVariable Integer id) { 
        return ResponseEntity.ok(personaService.findById(id));
    }

    @GetMapping("/email")
    @RequirePermission(module = "PERSONAS", permission = "READ")
    public ResponseEntity<List<PersonaResponseDTO>> getPersonasByEmail(@RequestParam String email) { 
        return ResponseEntity.ok(personaService.findByEmail(email));
    }

    @GetMapping("/telefono")
    @RequirePermission(module = "PERSONAS", permission = "READ")
    public ResponseEntity<List<PersonaResponseDTO>> getPersonasByTelefono(@RequestParam String telefono) { 
        return ResponseEntity.ok(personaService.findByTelefono(telefono));
    }

    @PostMapping
    @RequirePermission(module = "PERSONAS", permission = "CREATE")
    public ResponseEntity<PersonaResponseDTO> createPersona(@Valid @RequestBody PersonaRequestDTO personaRequestDTO) { 
        return ResponseEntity.status(HttpStatus.CREATED).body(personaService.save(personaRequestDTO));
    }

    @PatchMapping("/{id}")
    @RequirePermission(module = "PERSONAS", permission = "UPDATE")
    public ResponseEntity<PersonaResponseDTO> patchPersona(@PathVariable Integer id, @Valid @RequestBody PersonaRequestDTO personaRequestDTO) { 
        return ResponseEntity.ok(personaService.patch(id, personaRequestDTO));
    }

    @DeleteMapping("/{id}")
    @RequirePermission(module = "PERSONAS", permission = "DELETE")
    public ResponseEntity<Void> deletePersona(@PathVariable Integer id) {
        personaService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{personaId}/NaturalOrJuridica")
    @RequirePermission(module = "PERSONAS", permission = "READ")
    public ResponseEntity<PersonaDisplayDto> findPersonaNaturalOrJuridicaById(@PathVariable Integer personaId) {
        return ResponseEntity.ok(personaService.findPersonaNaturalOrJuridicaById(personaId));
    }
}