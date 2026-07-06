package com.everywhere.backend.service;

public interface CognitoAdminService {
    void createInvitedUser(String email);
    void deleteUser(String email);
}
