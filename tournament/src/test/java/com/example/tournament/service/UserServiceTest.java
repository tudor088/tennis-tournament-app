package com.example.tournament.service;

import com.example.tournament.dto.LoginDTO;
import com.example.tournament.entity.*;
import com.example.tournament.repository.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;                 // JUnit 5
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;           // Mockito
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.*;

import static org.assertj.core.api.Assertions.*; // AssertJ
import static org.mockito.Mockito.*;

class UserServiceTest {

    @Mock UserRepository repo;
    @Mock
    BCryptPasswordEncoder enc;
    @InjectMocks UserService service;
    AutoCloseable close;

    @BeforeEach void init(){ close = MockitoAnnotations.openMocks(this); }
    @AfterEach  void tear() throws Exception { close.close(); }

    @Test
    void login_success_returnsUser() {
        User u = User.builder().id(1L).email("a@b").password("hash").build();
        when(repo.findByEmail("a@b")).thenReturn(Optional.of(u));
        when(enc.matches("pw","hash")).thenReturn(true);

        Optional<User> result = service.login(new LoginDTO(){{ setEmail("a@b"); setPassword("pw"); }});

        assertThat(result).contains(u);
    }
}
