package com.example.tournament.service;

import com.example.tournament.entity.*;
import com.example.tournament.repository.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;                 // JUnit 5
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;           // Mockito
import java.util.*;

import static org.assertj.core.api.Assertions.*; // AssertJ
import static org.mockito.Mockito.*;

class RegistrationRequestServiceTest {

    @Mock RegistrationRequestRepository reqRepo;
    @Mock UserRepository userRepo;
    @Mock TournamentRepository tourRepo;
    @Mock EmailService email;
    @InjectMocks RegistrationRequestService service;

    AutoCloseable close;

    @BeforeEach void init() { close = MockitoAnnotations.openMocks(this); }
    @AfterEach  void tear() throws Exception { close.close(); }

    /* ---------- approve() happy-path ---------- */
    @Test
    void approve_marksRequestAndSendsMail() {
        User player = User.builder()
                .id(1L).username("anna").role(Role.PLAYER)
                .tournaments(new ArrayList<>())          // ← avoid NPE
                .build();
        Tournament t = Tournament.builder().id(5L).name("Madrid").build();
        RegistrationRequest rr = RegistrationRequest.builder()
                .id(10L).player(player).tournament(t)
                .status(RegistrationStatus.PENDING)
                .build();

        when(reqRepo.findById(10L)).thenReturn(Optional.of(rr));

        service.approve(10L);

        assertThat(rr.getStatus()).isEqualTo(RegistrationStatus.APPROVED);
        assertThat(player.getTournaments()).contains(t);
        verify(email).sendRegistrationDecision(player, t, true);
    }

    /* ---------- createRequest() duplicate-guard ---------- */
    @Test
    void createRequest_rejectsIfDuplicate() {
        long uid = 1L, tid = 5L;

        // minimal stubs so code reaches 'duplicate exists' branch
        when(userRepo.findById(uid))
                .thenReturn(Optional.of(User.builder().id(uid).role(Role.PLAYER).build()));
        when(tourRepo.findById(tid))
                .thenReturn(Optional.of(Tournament.builder().id(tid).build()));
        when(reqRepo.findByPlayerIdAndTournamentId(uid, tid))
                .thenReturn(Optional.of(new RegistrationRequest()));   // duplicate exists

        assertThatThrownBy(() -> service.createRequest(tid, uid))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("already exists");
    }
}
