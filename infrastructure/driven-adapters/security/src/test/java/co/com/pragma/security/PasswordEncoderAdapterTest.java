package co.com.pragma.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class PasswordEncoderAdapterTest {

    @Mock
    private org.springframework.security.crypto.password.PasswordEncoder springSecurityEncoder;

    @InjectMocks
    private PasswordEncoderAdapter adapter;

    private String rawPassword;
    private String encodedPassword;

    @BeforeEach
    void setup() {
        rawPassword = "mySecret123";
        encodedPassword = "$2a$10$abcdefg1234567";
    }

    @Test
    @DisplayName("Should encode password using Spring Security encoder")
    void testEncode() {
        when(springSecurityEncoder.encode(rawPassword)).thenReturn(encodedPassword);

        String result = adapter.encode(rawPassword);

        assertEquals(encodedPassword, result);
        verify(springSecurityEncoder).encode(rawPassword);
    }

    @Test
    @DisplayName("Should return true when raw password matches encoded password")
    void testMatchesTrue() {
        when(springSecurityEncoder.matches(rawPassword, encodedPassword)).thenReturn(true);

        boolean result = adapter.matches(rawPassword, encodedPassword);

        assertTrue(result);
        verify(springSecurityEncoder).matches(rawPassword, encodedPassword);
    }

    @Test
    @DisplayName("Should return false when raw password does not match encoded password")
    void testMatchesFalse() {
        when(springSecurityEncoder.matches(rawPassword, encodedPassword)).thenReturn(false);

        boolean result = adapter.matches(rawPassword, encodedPassword);

        assertFalse(result);
        verify(springSecurityEncoder).matches(rawPassword, encodedPassword);
    }
}
