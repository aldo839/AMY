package service;

import com.techdrawer.amy.dto.UserRegistrationDTO;
import com.techdrawer.amy.dto.UserResponseDTO;
import com.techdrawer.amy.model.Role;
import com.techdrawer.amy.model.User;
import com.techdrawer.amy.repository.UserRepository;
import com.techdrawer.amy.service.AuthService;
import com.techdrawer.amy.service.UserMapperService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private UserMapperService userMapperService;

    @InjectMocks
    private AuthService authService;

    @Test
    void testRegisterArtist_Success() {

        // Arrange
        UserRegistrationDTO dto = new UserRegistrationDTO("testartist1", "testartist1@gmail.com", "TestArtist1Password");

        UserResponseDTO expectedResponse = new UserResponseDTO(
                UUID.randomUUID(),
                "testartist1",
                "testartist1@gmail.com",
                Role.ARTIST
        );

        // Act
        User user = new User();
        user.setName(dto.getName());
        user.setEmail(dto.getEmail());
        user.setPassword(dto.getPassword());

        when(userRepository.existsByEmail(dto.getEmail())).thenReturn(false);

        when(passwordEncoder.encode(dto.getPassword())).thenReturn("encoded_password");

        when(userRepository.save(any(User.class))).thenAnswer(invocationOnMock -> {
            User u = invocationOnMock.getArgument(0);
            u.setId(1L);
            return u;
        });

        when(userMapperService.mapToResponseDTO(any(User.class))).thenReturn(expectedResponse);

        UserResponseDTO registeredUser = authService.registerArtist(dto);

        // Assert
        assertNotNull(registeredUser);
        assertEquals("testartist1@gmail.com", expectedResponse.email());

        verify(userRepository, times(1)).existsByEmail(dto.getEmail());
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void testRegister_EmailAlreadyExists() {
        // Arrange
        UserRegistrationDTO dto = new UserRegistrationDTO("testartist2", "testartist1@gmail.com", "TestArtist1Password");

        User newUser = new User();
        newUser.setName(dto.getName());
        newUser.setEmail(dto.getEmail());
        newUser.setPassword(dto.getPassword());

        // Act
        when(userRepository.existsByEmail("testartist1@gmail.com")).thenReturn(true);

        assertThrows(RuntimeException.class, () -> {
            authService.registerArtist(dto);
        });

        // Assert
        verify(userRepository, never()).save(any(User.class));
    }

}
