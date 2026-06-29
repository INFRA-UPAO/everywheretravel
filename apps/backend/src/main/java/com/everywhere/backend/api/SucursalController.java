package com.everywhere.backend.api;

import com.everywhere.backend.model.dto.SucursalRequestDTO;
import com.everywhere.backend.model.dto.SucursalResponseDTO;
import com.everywhere.backend.security.RequirePermission;
import com.everywhere.backend.service.SucursalService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/sucursales")
@RequiredArgsConstructor
public class SucursalController {

    private final SucursalService sucursalService;

    @GetMapping
    @RequirePermission(module = "SUCURSALES", permission = "READ")
    public ResponseEntity<List<SucursalResponseDTO>> getAllSucursales() { 
        return ResponseEntity.ok(sucursalService.findAll());
    }

    @GetMapping("/{id}")
    @RequirePermission(module = "SUCURSALES", permission = "READ")
    public ResponseEntity<SucursalResponseDTO> getSucursalById(@PathVariable Integer id) { 
        return ResponseEntity.ok(sucursalService.findById(id));
    }

    @GetMapping("/estado/{estado}")
    @RequirePermission(module = "SUCURSALES", permission = "READ")
    public ResponseEntity<List<SucursalResponseDTO>> getSucursalesByEstado(@PathVariable Boolean estado) { 
        return ResponseEntity.ok(sucursalService.findByEstado(estado));
    }

    @PostMapping
    @RequirePermission(module = "SUCURSALES", permission = "CREATE")
    public ResponseEntity<SucursalResponseDTO> createSucursal(@Valid @RequestBody SucursalRequestDTO sucursalRequestDTO) { 
        return ResponseEntity.status(HttpStatus.CREATED).body(sucursalService.save(sucursalRequestDTO));
    }

    @PatchMapping("/{id}")
    @RequirePermission(module = "SUCURSALES", permission = "UPDATE")
    public ResponseEntity<SucursalResponseDTO> updateSucursal(
            @PathVariable Integer id,
            @Valid @RequestBody SucursalRequestDTO sucursalRequestDTO) { 
        return ResponseEntity.ok(sucursalService.update(id, sucursalRequestDTO));
    }

    @PatchMapping("/{id}/estado")
    @RequirePermission(module = "SUCURSALES", permission = "UPDATE")
    public ResponseEntity<SucursalResponseDTO> cambiarEstadoSucursal(
            @PathVariable Integer id,
            @RequestParam Boolean estado) { 
        return ResponseEntity.ok(sucursalService.cambiarEstado(id, estado));
    }

    @DeleteMapping("/{id}")
    @RequirePermission(module = "SUCURSALES", permission = "DELETE")
    public ResponseEntity<Void> deleteSucursal(@PathVariable Integer id) {
        sucursalService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
