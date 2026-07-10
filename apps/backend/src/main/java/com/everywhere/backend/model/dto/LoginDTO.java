package com.everywhere.backend.model.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LoginDTO {
    @NotBlank(message = "El correo electronico es obligatorio")
    @Email(message = "El correo electronico no es valido" )
    private String email;
    @NotBlank(message = "La contraseña es obligatoria")
    private String password;
}
