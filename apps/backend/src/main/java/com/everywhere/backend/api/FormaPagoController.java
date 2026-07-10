package com.everywhere.backend.api;

import com.everywhere.backend.model.dto.FormaPagoRequestDTO;
import com.everywhere.backend.model.dto.FormaPagoResponseDTO;
import com.everywhere.backend.security.RequirePermission;
import com.everywhere.backend.service.FormaPagoService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/formas-pago")
@AllArgsConstructor
public class FormaPagoController {

    @Autowired
    private FormaPagoService formaPagoService;

    @GetMapping
    @RequirePermission(module = "FORMA-PAGO", permission = "READ")
    public ResponseEntity<List<FormaPagoResponseDTO>> getAllFormasPago() { 
        return ResponseEntity.ok(formaPagoService.findAll());
    }

    @GetMapping("/{id}")
    @RequirePermission(module = "FORMA-PAGO", permission = "READ")
    public ResponseEntity<FormaPagoResponseDTO> getFormaPagoById(@PathVariable Integer id) { 
        return ResponseEntity.ok(formaPagoService.findById(id));
    }

    @GetMapping("/codigo/{codigo}")
    @RequirePermission(module = "FORMA-PAGO", permission = "READ")
    public ResponseEntity<FormaPagoResponseDTO> getFormaPagoByCodigo(@PathVariable Integer codigo) { 
        return ResponseEntity.ok(formaPagoService.findByCodigo(codigo));
    }

    @GetMapping("/descripcion")
    @RequirePermission(module = "FORMA-PAGO", permission = "READ")
    public ResponseEntity<List<FormaPagoResponseDTO>> getFormasPagoByDescripcion(@RequestParam String descripcion) { 
        return ResponseEntity.ok(formaPagoService.findByDescripcion(descripcion));
    }

    @PostMapping
    @RequirePermission(module = "FORMA-PAGO", permission = "CREATE")
    public ResponseEntity<FormaPagoResponseDTO> createFormaPago(@Valid @RequestBody FormaPagoRequestDTO formaPagoRequestDTO) { 
        return ResponseEntity.status(HttpStatus.CREATED).body(formaPagoService.save(formaPagoRequestDTO));
    }

    @PatchMapping("/{id}")
    @RequirePermission(module = "FORMA-PAGO", permission = "UPDATE")
    public ResponseEntity<FormaPagoResponseDTO> updateFormaPago(@PathVariable Integer id, @Valid @RequestBody FormaPagoRequestDTO formaPagoRequestDTO) { 
        return ResponseEntity.ok(formaPagoService.update(id, formaPagoRequestDTO));
    }

    @DeleteMapping("/{id}")
    @RequirePermission(module = "FORMA-PAGO", permission = "DELETE")
    public ResponseEntity<Void> deleteFormaPago(@PathVariable Integer id) {
        formaPagoService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
