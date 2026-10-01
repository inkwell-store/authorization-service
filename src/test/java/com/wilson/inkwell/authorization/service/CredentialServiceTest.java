package com.wilson.inkwell.authorization.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrowsExactly;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.wilson.inkwell.authorization.dto.AccessAndRefreshTokenResponse;
import com.wilson.inkwell.authorization.dto.CredentialCreationEvent;
import com.wilson.inkwell.authorization.dto.CredentialForm;
import com.wilson.inkwell.authorization.entity.Credential;
import com.wilson.inkwell.authorization.entity.Role;
import com.wilson.inkwell.authorization.enums.RoleEnum;
import com.wilson.inkwell.authorization.exception.CredentialAlreadyCreatedException;
import com.wilson.inkwell.authorization.repository.CredentialRepository;
import com.wilson.inkwell.authorization.repository.RefreshTokenRepository;
import com.wilson.inkwell.authorization.repository.RoleRepository;

@ExtendWith(MockitoExtension.class)
public class CredentialServiceTest {
    
    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private RabbitMqEventEmitterService rabbitMqEventEmitterService;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private CredentialRepository credentialRepository;

    @Mock
    private JwtService jwtService;

    @Mock
    private RefreshTokenService refreshTokenService;

    @Mock
    private RefreshTokenRepository refreshTokenRepository;

    @InjectMocks
    private CredentialService credentialService; // the real object i'm testing

    @Test
    void shouldRegisterNewCredential() {

        // 1: Arrange
        String refreshToken = "refresh-token";
        String accessToken = "access-token";
        CredentialForm form = new CredentialForm("test@gmail.com", "test-password");
        Role role = new Role();
        role.setName(RoleEnum.ROLE_CUSTOMER);

        when(credentialRepository.getCredentialsByEmail(form.email())).thenReturn(Optional.empty());
        when(roleRepository.getRoleByName(RoleEnum.ROLE_CUSTOMER)).thenReturn(Optional.of(role));
        when(passwordEncoder.encode(form.password())).thenReturn("hashed-enconded-password");
        when(jwtService.generateJwt(any())).thenReturn(accessToken);

        // retunr the entity I passed to the save()
        when(credentialRepository.save(any(Credential.class))).thenAnswer(
            invoc -> invoc.getArgument(0)
        );

        when(refreshTokenService.generateOpaqueRefreshToken()).thenReturn(refreshToken);
        when(refreshTokenService.hashOpaqueToken(refreshToken)).thenReturn("hashed-refresh-token");

        // 2: Act
        AccessAndRefreshTokenResponse response =  credentialService.registerNewCredentials(form);

        // 3: Assert
        // first the response must not be null
        assertNotNull(response);
        assertEquals(refreshToken, response.refreshToken());
        assertEquals(accessToken, response.accessToken());

        // see if the save() method was correctly called with a Credential
        verify(credentialRepository).save(any(Credential.class));
    }

    @Test
    void shouldRejectAlreadyRegisteredEmail() {

        // 1: Arrange:
        CredentialForm form = new CredentialForm("test@gmail.com", "test-password");
        when(credentialRepository.getCredentialsByEmail(form.email())).thenReturn(
            Optional.of(new Credential())
        );

        // 2, 3: Act and Assert:
        assertThrowsExactly(
            CredentialAlreadyCreatedException.class, 
            () -> credentialService.registerNewCredentials(form));

        // make sure no database call was made
        verify(credentialRepository, never()).save(any(Credential.class));

        // make sure no rabbitmq call is made
        verify(rabbitMqEventEmitterService, never()).sendCredentialCreationMessage(
            any(CredentialCreationEvent.class)
        );
    }
}
