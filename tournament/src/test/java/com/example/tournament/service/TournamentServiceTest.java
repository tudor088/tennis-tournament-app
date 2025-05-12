package com.example.tournament.service;

import com.example.tournament.entity.*;
import com.example.tournament.repository.TournamentRepository;
import com.example.tournament.repository.UserRepository;
import org.junit.jupiter.api.*;
import org.mockito.*;

import java.util.*;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class TournamentServiceTest {

    @Mock TournamentRepository tourRepo;
    @Mock UserRepository userRepo;
    @InjectMocks TournamentService service;
    AutoCloseable close;

    @BeforeEach void init() { close = MockitoAnnotations.openMocks(this); }
    @AfterEach  void tear() throws Exception { close.close(); }

    @Test
    void registerPlayer_rejectsIfNotPlayerRole() {
        User ref = User.builder().id(2L).role(Role.REFEREE).build();
        when(userRepo.findById(2L)).thenReturn(Optional.of(ref));

        assertThatThrownBy(() -> service.registerPlayer(1L, 2L))
                .hasMessageContaining("Only players");
    }

    @Test
    void registerPlayer_successAddsTournament() {
        long uid = 5L, tid = 9L;
        User player = User.builder()
                .id(uid).role(Role.PLAYER)
                .tournaments(new ArrayList<>())
                .build();
        Tournament t = Tournament.builder().id(tid).name("Canada").build();

        when(userRepo.findById(uid)).thenReturn(Optional.of(player));
        when(tourRepo.findById(tid)).thenReturn(Optional.of(t));

        String msg = service.registerPlayer(tid, uid);

        assertThat(player.getTournaments()).containsExactly(t);
        assertThat(msg).contains("joined Canada");
        verify(userRepo).save(player);
    }
}
