package com.everywhere.backend.service.impl;

import com.everywhere.backend.exceptions.UserNotFoundException;
import com.everywhere.backend.mapper.UserMapper;
import com.everywhere.backend.model.dto.*;

import com.everywhere.backend.model.entity.User;
import com.everywhere.backend.repository.UserRepository;
import com.everywhere.backend.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

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
}
