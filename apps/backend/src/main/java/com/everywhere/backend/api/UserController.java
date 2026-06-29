package com.everywhere.backend.api;

import com.everywhere.backend.model.dto.UpdateUserNameDTO;
import com.everywhere.backend.model.dto.UserProfileDTO;
import com.everywhere.backend.security.SecurityContextHelper;
import com.everywhere.backend.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final SecurityContextHelper securityContextHelper;

    @GetMapping("/me")
    public ResponseEntity<UserProfileDTO> getCurrentUserProfile() {
        Integer userId = securityContextHelper.getCurrentUser().getId();
        return ResponseEntity.ok(userService.getUserProfile(userId));
    }

    @PatchMapping("/me")
    public ResponseEntity<UserProfileDTO> updateCurrentUserName(@RequestBody UpdateUserNameDTO request) {
        Integer userId = securityContextHelper.getCurrentUser().getId();
        String name = request != null ? request.getName() : null;
        return ResponseEntity.ok(userService.updateUserName(userId, name));
    }
}
