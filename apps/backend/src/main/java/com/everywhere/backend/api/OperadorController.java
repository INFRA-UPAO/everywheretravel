package com.everywhere.backend.api;

import com.everywhere.backend.model.dto.OperadorRequestDTO;
import com.everywhere.backend.model.dto.OperadorResponseDTO;
import com.everywhere.backend.security.RequirePermission;
import com.everywhere.backend.service.OperadorService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;


@RestController
@RequestMapping("/operadores")
@RequiredArgsConstructor
public class OperadorController {

    private final OperadorService operadorService;

    @GetMapping
    @RequirePermission(module = "OPERADOR", permission = "READ")
    public ResponseEntity<List<OperadorResponseDTO>> findAll() { 
        return ResponseEntity.ok(operadorService.findAll());
    }

    @GetMapping("/{id}")
    @RequirePermission(module = "OPERADOR", permission = "READ")
    public ResponseEntity<OperadorResponseDTO> getById(@PathVariable Integer id) {
        return ResponseEntity.ok(operadorService.findById(id));
    }

    @GetMapping("/nombre")
    public ResponseEntity<OperadorResponseDTO> getByNombre(@RequestParam String nombre) {
        return ResponseEntity.ok(operadorService.findByNombre(nombre));
    }

    @PostMapping
    @RequirePermission(module = "OPERADOR", permission = "CREATE")
    public ResponseEntity<OperadorResponseDTO> create(@RequestBody OperadorRequestDTO operadorRequestDTO) { 
        return ResponseEntity.status(HttpStatus.CREATED).body(operadorService.save(operadorRequestDTO));
    }

    @PatchMapping("/{id}")
    @RequirePermission(module = "OPERADOR", permission = "UPDATE")
    public ResponseEntity<OperadorResponseDTO> partialUpdate(
            @PathVariable Integer id,
            @RequestBody OperadorRequestDTO dto) {
            return ResponseEntity.ok(operadorService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    @RequirePermission(module = "OPERADOR", permission = "DELETE")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        operadorService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
