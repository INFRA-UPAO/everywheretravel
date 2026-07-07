package com.everywhere.backend.service.impl;

import com.everywhere.backend.exceptions.ConflictException;
import com.everywhere.backend.service.CognitoAdminService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.cognitoidentityprovider.CognitoIdentityProviderClient;
import software.amazon.awssdk.services.cognitoidentityprovider.model.AdminCreateUserRequest;
import software.amazon.awssdk.services.cognitoidentityprovider.model.AdminDeleteUserRequest;
import software.amazon.awssdk.services.cognitoidentityprovider.model.AttributeType;
import software.amazon.awssdk.services.cognitoidentityprovider.model.UsernameExistsException;

@Service
@RequiredArgsConstructor
@Slf4j
public class CognitoAdminServiceImpl implements CognitoAdminService {

    private final CognitoIdentityProviderClient cognitoIdentityProviderClient;

    @Value("${app.cognito.user-pool-id}")
    private String userPoolId;

    @Override
    public void createInvitedUser(String email) {
        try {
            cognitoIdentityProviderClient.adminCreateUser(AdminCreateUserRequest.builder()
                    .userPoolId(userPoolId)
                    .username(email)
                    .userAttributes(
                            AttributeType.builder().name("email").value(email).build(),
                            AttributeType.builder().name("email_verified").value("true").build())
                    .build());
            log.info("Usuario creado en Cognito, invitacion enviada a: {}", email);
        } catch (UsernameExistsException e) {
            throw new ConflictException("Ya existe un usuario en Cognito con el email: " + email);
        }
    }

    @Override
    public void deleteUser(String email) {
        cognitoIdentityProviderClient.adminDeleteUser(AdminDeleteUserRequest.builder()
                .userPoolId(userPoolId)
                .username(email)
                .build());
        log.warn("Usuario eliminado de Cognito por rollback: {}", email);
    }
}
