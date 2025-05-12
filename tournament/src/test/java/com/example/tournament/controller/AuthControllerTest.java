package com.example.tournament.controller;

import com.example.tournament.dto.LoginDTO;
import com.example.tournament.dto.RegisterDTO;
import com.example.tournament.entity.Role;
import com.example.tournament.entity.User;
import com.example.tournament.service.UserService;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthController.class)
@AutoConfigureMockMvc(addFilters = false)
class AuthControllerTest {

    @Autowired MockMvc mvc;
    @MockBean UserService userService;   // → injected into controller

    @Test
    void login_successReturns200AndJson() throws Exception {
        User u = User.builder().id(1L).username("bob").email("a@b").role(Role.PLAYER).build();
        when(userService.login(any(LoginDTO.class))).thenReturn(Optional.of(u));

        mvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                         {"email":"a@b","password":"secret"}
                         """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.username").value("bob"));
    }

    @Test
    void login_wrongPasswordReturns401() throws Exception {
        when(userService.login(any(LoginDTO.class))).thenReturn(Optional.empty());

        mvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                         {"email":"a@b","password":"wrong"}
                         """))
                .andExpect(status().isUnauthorized());
    }
}
