package com.everywhere.backend.api;

import com.everywhere.backend.model.dto.DetalleReciboRequestDTO;
import com.everywhere.backend.model.dto.DetalleReciboResponseDTO;
import com.everywhere.backend.security.RequirePermission;
import com.everywhere.backend.service.DetalleReciboService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/detalle-recibo")
@RequiredArgsConstructor
public class DetalleReciboController {

    private final DetalleReciboService detalleService;

    @GetMapping
    @RequirePermission(module = "RECIBOS", permission = "READ")
    public ResponseEntity<List<DetalleReciboResponseDTO>> getAllDetalles() { 
        return ResponseEntity.ok(detalleService.findAll());
    }

    @GetMapping("/{id}")
    @RequirePermission(module = "RECIBOS", permission = "READ")
    public ResponseEntity<DetalleReciboResponseDTO> getDetalleById(@PathVariable Integer id) {
        return ResponseEntity.ok(detalleService.findById(id));
    }

    @GetMapping("/recibo/{reciboId}")
    @RequirePermission(module = "RECIBOS", permission = "READ")
    public ResponseEntity<List<DetalleReciboResponseDTO>> getDetallesByRecibo(@PathVariable Integer reciboId) {
        return ResponseEntity.ok(detalleService.findByReciboId(reciboId));
    }

    @PostMapping
    @RequirePermission(module = "RECIBOS", permission = "CREATE")
    public ResponseEntity<DetalleReciboResponseDTO> createDetalle(@Valid @RequestBody DetalleReciboRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(detalleService.save(dto));
    }

    @PatchMapping("/{id}")
    @RequirePermission(module = "RECIBOS", permission = "UPDATE")
    public ResponseEntity<DetalleReciboResponseDTO> updateDetalle(
            @PathVariable Integer id, @Valid @RequestBody DetalleReciboRequestDTO detalleReciboRequestDTO) { 
        return ResponseEntity.ok(detalleService.patch(id, detalleReciboRequestDTO));
    }

    @DeleteMapping("/{id}")
    @RequirePermission(module = "RECIBOS", permission = "DELETE")
    public ResponseEntity<Void> deleteDetalle(@PathVariable Integer id) {
        detalleService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
