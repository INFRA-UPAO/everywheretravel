package com.everywhere.backend.api;

import com.everywhere.backend.model.dto.PersonaJuridicaRequestDTO;
import com.everywhere.backend.model.dto.PersonaJuridicaResponseDTO;
import com.everywhere.backend.security.RequirePermission;
import com.everywhere.backend.service.PersonaJuridicaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/personas-juridicas")
@RequiredArgsConstructor
public class PersonaJuridicaController {

    private final PersonaJuridicaService personaJuridicaService;

    @GetMapping
    @RequirePermission(module = "PERSONAS", permission = "READ")
    public ResponseEntity<List<PersonaJuridicaResponseDTO>> getAllPersonasJuridicas() { 
        return ResponseEntity.ok(personaJuridicaService.findAll());
    }

    @GetMapping("/ruc")
    @RequirePermission(module = "PERSONAS", permission = "READ")
    public ResponseEntity<List<PersonaJuridicaResponseDTO>> getPersonasJuridicasByRUC(@RequestParam String ruc) { 
        return ResponseEntity.ok(personaJuridicaService.findByRuc(ruc.trim()));
    }

    @GetMapping("/razSocial")
    @RequirePermission(module = "PERSONAS", permission = "READ")
    public ResponseEntity<List<PersonaJuridicaResponseDTO>> getPersonasJuridicasByRazSocial(@RequestParam String razonSocial) { 
        return ResponseEntity.ok(personaJuridicaService.findByRazonSocial(razonSocial.trim()));
    }

    @GetMapping("/{id}")
    @RequirePermission(module = "PERSONAS", permission = "READ")
    public ResponseEntity<PersonaJuridicaResponseDTO> getPersonaJuridicaById(@PathVariable Integer id) { 
        return ResponseEntity.ok(personaJuridicaService.findById(id));
    }

    @PostMapping
    @RequirePermission(module = "PERSONAS", permission = "CREATE")
    public ResponseEntity<PersonaJuridicaResponseDTO> createPersonaJuridica(@Valid @RequestBody PersonaJuridicaRequestDTO personaJuridicaRequestDTO) { 
        return ResponseEntity.status(HttpStatus.CREATED).body(personaJuridicaService.save(personaJuridicaRequestDTO));
    }

    @PatchMapping("/{id}")
    @RequirePermission(module = "PERSONAS", permission = "UPDATE")
    public ResponseEntity<PersonaJuridicaResponseDTO> patchPersonaJuridica(@PathVariable Integer id, @Valid @RequestBody PersonaJuridicaRequestDTO personaJuridicaRequestDTO) {
        return ResponseEntity.ok(personaJuridicaService.patch(id, personaJuridicaRequestDTO));
    }

    @DeleteMapping("/{id}")
    @RequirePermission(module = "PERSONAS", permission = "DELETE")
    public ResponseEntity<Void> deletePersonaJuridica(@PathVariable Integer id) {
        personaJuridicaService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
