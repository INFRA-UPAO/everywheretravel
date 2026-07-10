package com.everywhere.backend.mapper;

import com.everywhere.backend.model.dto.*;
import com.everywhere.backend.model.entity.User;
import com.everywhere.backend.model.enums.Role;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserMapper {
    private final ModelMapper modelMapper;

    // Convierte LoginDTO a User entity (para autenticación)
    public User toUserEntity(LoginDTO loginDTO) {
        return modelMapper.map(loginDTO, User.class);
    }

    // Convierte User entity a AuthResponseDTO (respuesta de login con JWT)
    public AuthResponseDTO toAuthResponseDTO(User user, String token) {
        if (user == null) {
            throw new IllegalArgumentException("El usuario no puede ser nulo");
        }
        
        if (user.getRole() == null) {
            throw new IllegalArgumentException("El usuario no tiene un rol asignado");
        }

        AuthResponseDTO authResponseDTO = new AuthResponseDTO();

        authResponseDTO.setId(user.getId());
        authResponseDTO.setToken(token);
        
        Role role = Role.fromName(user.getRole().getName()); // Obtener rol como enum para acceder a permisos
        authResponseDTO.setRole(role.getName());

        authResponseDTO.setPermissions(role.getModulePermissions()); // Cargar permisos desde el rol, no desde el usuario
        authResponseDTO.setName(user.getNombre()); // Usar email como nombre temporal
        authResponseDTO.setPermissions(role.getModulePermissions());

        return authResponseDTO;
    }

    // Convierte User básico a DTO simple
    public UserBasicDTO toUserBasicDTO(User user) {
        UserBasicDTO userBasicDTO = new UserBasicDTO();
        userBasicDTO.setId(user.getId());
        userBasicDTO.setEmail(user.getEmail());
        userBasicDTO.setName(user.getNombre()); // Usar email como identificador
        return userBasicDTO;
    }

    public UserProfileDTO toUserProfileDTO(User user) {
        if (user == null) {
            throw new IllegalArgumentException("El usuario no puede ser nulo");
        }

        UserProfileDTO profile = new UserProfileDTO();
        profile.setId(user.getId());
        profile.setName(user.getNombre());
        profile.setEmail(user.getEmail());
        profile.setRole(user.getRole() != null ? user.getRole().getName() : null);

        if (user.getSucursal() != null) {
            SucursalResponseDTO sucursal = modelMapper.map(user.getSucursal(), SucursalResponseDTO.class);
            profile.setSucursal(sucursal);
        }

        return profile;
    }
}