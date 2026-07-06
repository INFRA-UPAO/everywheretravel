package com.everywhere.backend.service.impl;

import com.everywhere.backend.exceptions.ConflictException;
import com.everywhere.backend.exceptions.ResourceNotFoundException;
import com.everywhere.backend.exceptions.UserNotFoundException;
import com.everywhere.backend.mapper.UserMapper;
import com.everywhere.backend.model.dto.*;

import com.everywhere.backend.model.entity.Role;
import com.everywhere.backend.model.entity.Sucursal;
import com.everywhere.backend.model.entity.User;
import com.everywhere.backend.repository.RoleRepository;
import com.everywhere.backend.repository.SucursalRepository;
import com.everywhere.backend.repository.UserRepository;
import com.everywhere.backend.service.CognitoAdminService;
import com.everywhere.backend.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final SucursalRepository sucursalRepository;
    private final UserMapper userMapper;
    private final CognitoAdminService cognitoAdminService;

    @Override
    @Transactional(readOnly = true)
    public User getUserbyId(Integer userId) {
        return userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException("Usuario no encontrado con ID: " + userId));
    }

    @Override
    @Transactional(readOnly = true)
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public UserBasicDTO getUserBasicInfo(Integer userId) {
        User user = getUserbyId(userId);
        return userMapper.toUserBasicDTO(user);
    }

    @Override
    @Transactional(readOnly = true)
    public UserProfileDTO getUserProfile(Integer userId) {
        User user = getUserbyId(userId);
        return userMapper.toUserProfileDTO(user);
    }

    @Override
    @Transactional
    public UserProfileDTO updateUserName(Integer userId, String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre no puede estar vacio");
        }
        User user = getUserbyId(userId);
        user.setNombre(name.trim());
        return userMapper.toUserProfileDTO(userRepository.save(user));
    }

    @Override
    @Transactional
    public UserProfileDTO createUser(CreateUserRequestDTO request) {
        String email = request.getEmail() != null ? request.getEmail().trim().toLowerCase() : null;
        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("El email es obligatorio");
        }
        if (userRepository.findByEmail(email).isPresent()) {
            throw new ConflictException("Ya existe un usuario con el email: " + email);
        }

        Role role = roleRepository.findById(request.getRolId())
                .orElseThrow(() -> new ResourceNotFoundException("Rol no encontrado con ID: " + request.getRolId()));

        Sucursal sucursal = null;
        if (request.getSucursalId() != null) {
            sucursal = sucursalRepository.findById(request.getSucursalId())
                    .orElseThrow(() -> new ResourceNotFoundException("Sucursal no encontrada con ID: " + request.getSucursalId()));
        }

        cognitoAdminService.createInvitedUser(email);

        try {
            User user = new User();
            user.setEmail(email);
            user.setNombre(request.getNombre());
            user.setPassword("COGNITO_MANAGED");
            user.setRole(role);
            user.setSucursal(sucursal);
            return userMapper.toUserProfileDTO(userRepository.save(user));
        } catch (RuntimeException e) {
            log.error("Fallo al guardar el usuario en BD tras crearlo en Cognito, revirtiendo: {}", email, e);
            cognitoAdminService.deleteUser(email);
            throw e;
        }
    }
}
