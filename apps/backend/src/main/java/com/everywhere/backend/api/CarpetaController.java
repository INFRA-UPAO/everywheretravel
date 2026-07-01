package com.everywhere.backend.api;

import com.everywhere.backend.exceptions.BadRequestException;
import com.everywhere.backend.model.dto.CarpetaRequestDto;
import com.everywhere.backend.model.dto.CarpetaResponseDto;
import com.everywhere.backend.security.RequirePermission;
import com.everywhere.backend.service.CarpetaService;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*; 

import java.time.LocalDate;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/carpeta")
public class CarpetaController {

    private final CarpetaService carpetaService;

    @PostMapping
    @RequirePermission(module = "CARPETA", permission = "CREATE")
    public ResponseEntity<CarpetaResponseDto> create(
            @RequestBody CarpetaRequestDto carpetaRequestDto,
            @RequestParam(required = false) Integer carpetaPadreId,
            HttpServletRequest request) {
                
        if (!request.getParameterMap().isEmpty() && request.getParameter("carpetaPadreId") == null)
                throw new BadRequestException("El parámetro 'carpetaPadreId' está mal escrito o no es válido.");
        
        return ResponseEntity.status(HttpStatus.CREATED).body(carpetaService.create(carpetaRequestDto, carpetaPadreId));
    }

    @GetMapping("/{id}")
    @RequirePermission(module = "CARPETA", permission = "READ")
    public ResponseEntity<CarpetaResponseDto> findById(@PathVariable Integer id) {
        return ResponseEntity.ok(carpetaService.findById(id));
    }

    @GetMapping
    @RequirePermission(module = "CARPETA", permission = "READ")
    public ResponseEntity<List<CarpetaResponseDto>> findAll() {
        return ResponseEntity.ok(carpetaService.findAll());
    }

    @PatchMapping("/{id}")
    @RequirePermission(module = "CARPETA", permission = "UPDATE")
    public ResponseEntity<CarpetaResponseDto> update(@PathVariable Integer id, @RequestBody CarpetaRequestDto carpetaRequestDto) {
        return ResponseEntity.ok(carpetaService.update(id, carpetaRequestDto));
    }

    @DeleteMapping("/{id}")
    @RequirePermission(module = "CARPETA", permission = "DELETE")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        carpetaService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/padre/{carpetaPadreId}")
    @RequirePermission(module = "CARPETA", permission = "READ")
    public ResponseEntity<List<CarpetaResponseDto>> findByCarpetaPadre(@PathVariable Integer carpetaPadreId) {
        return ResponseEntity.ok(carpetaService.findByCarpetaPadreId(carpetaPadreId));
    }

    @GetMapping("/nivel/{nivel}")
    @RequirePermission(module = "CARPETA", permission = "READ")
    public ResponseEntity<List<CarpetaResponseDto>> findByNivel(@PathVariable Integer nivel) {
        return ResponseEntity.ok(carpetaService.findByNivel(nivel));
    }

    @GetMapping("/buscar")
    @RequirePermission(module = "CARPETA", permission = "READ")
    public ResponseEntity<List<CarpetaResponseDto>> findByNombre(@RequestParam String nombre) {
        return ResponseEntity.ok(carpetaService.findByNombre(nombre));
    }

    @GetMapping("/fecha/{mes}")
    @RequirePermission(module = "CARPETA", permission = "READ")
    public ResponseEntity<List<CarpetaResponseDto>> findByMes(@PathVariable int mes) {
        return ResponseEntity.ok(carpetaService.findByMes(mes));
    }

    @GetMapping("/fecha")
    @RequirePermission(module = "CARPETA", permission = "READ")
    public ResponseEntity<List<CarpetaResponseDto>> findByRango(@RequestParam LocalDate inicio, @RequestParam LocalDate fin) {
        return ResponseEntity.ok(carpetaService.findByFechaCreacionBetween(inicio, fin));
    }

    @GetMapping("/recientes")
    @RequirePermission(module = "CARPETA", permission = "READ")
    public ResponseEntity<List<CarpetaResponseDto>> findRecent(@RequestParam(defaultValue = "5") int limit) {
        return ResponseEntity.ok(carpetaService.findRecent(limit));
    }

    @GetMapping("/raices") // Listar raíces (sin padre)
    @RequirePermission(module = "CARPETA", permission = "READ")
    public ResponseEntity<List<CarpetaResponseDto>> findRaices() {
        return ResponseEntity.ok(carpetaService.findRaices());
    }

    @GetMapping("/{id}/camino")
    @RequirePermission(module = "CARPETA", permission = "READ")
    public ResponseEntity<List<CarpetaResponseDto>> findCamino(@PathVariable Integer id) {
        return ResponseEntity.ok(carpetaService.findCamino(id));
    }

    @GetMapping("/hijos/{id}")
    @RequirePermission(module = "CARPETA", permission = "READ")
    public ResponseEntity<List<CarpetaResponseDto>> findHijos(@PathVariable Integer id) {
        return ResponseEntity.ok(carpetaService.findByCarpetaPadreId(id));
    }

}