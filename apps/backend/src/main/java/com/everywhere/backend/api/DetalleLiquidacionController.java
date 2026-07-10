package com.everywhere.backend.api;

import com.everywhere.backend.model.dto.DetalleLiquidacionRequestDTO;
import com.everywhere.backend.model.dto.DetalleLiquidacionResponseDTO;
import com.everywhere.backend.model.dto.DetalleLiquidacionSinLiquidacionDTO;
import com.everywhere.backend.security.RequirePermission;
import com.everywhere.backend.service.DetalleLiquidacionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/detalles-liquidacion")
@RequiredArgsConstructor
public class DetalleLiquidacionController {

    private final DetalleLiquidacionService detalleLiquidacionService;

    @GetMapping
    @RequirePermission(module = "LIQUIDACIONES", permission = "READ")
    public ResponseEntity<List<DetalleLiquidacionResponseDTO>> getAllDetallesLiquidacion() { 
        return ResponseEntity.ok(detalleLiquidacionService.findAll());
    }

    @GetMapping("/{id}")
    @RequirePermission(module = "LIQUIDACIONES", permission = "READ")
    public ResponseEntity<DetalleLiquidacionResponseDTO> getDetalleLiquidacionById(@PathVariable Integer id) { 
        return ResponseEntity.ok(detalleLiquidacionService.findById(id));
    }

    @GetMapping("/liquidacion/{liquidacionId}")
    @RequirePermission(module = "LIQUIDACIONES", permission = "READ")
    public ResponseEntity<List<DetalleLiquidacionSinLiquidacionDTO>> getDetallesByLiquidacionId(@PathVariable Integer liquidacionId) { 
        return ResponseEntity.ok(detalleLiquidacionService.findByLiquidacionIdSinLiquidacion(liquidacionId));
    }

    @PostMapping
    @RequirePermission(module = "LIQUIDACIONES", permission = "CREATE")
    public ResponseEntity<DetalleLiquidacionResponseDTO> createDetalleLiquidacion(
            @Valid @RequestBody DetalleLiquidacionRequestDTO detalleLiquidacionRequestDTO) {
        return ResponseEntity.status(HttpStatus.CREATED).body(detalleLiquidacionService.save(detalleLiquidacionRequestDTO));
    }

    @PatchMapping("/{id}")
    @RequirePermission(module = "LIQUIDACIONES", permission = "UPDATE")
    public ResponseEntity<DetalleLiquidacionResponseDTO> updateDetalleLiquidacion(
            @PathVariable Integer id, @RequestBody DetalleLiquidacionRequestDTO detalleLiquidacionRequestDTO) {
        return ResponseEntity.ok(detalleLiquidacionService.update(id, detalleLiquidacionRequestDTO));
    }

    @DeleteMapping("/{id}")
    @RequirePermission(module = "LIQUIDACIONES", permission = "DELETE")
    public ResponseEntity<Void> deleteDetalleLiquidacion(@PathVariable Integer id) {
        detalleLiquidacionService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}