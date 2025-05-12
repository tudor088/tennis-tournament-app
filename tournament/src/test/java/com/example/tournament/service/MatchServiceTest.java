package com.example.tournament.service;

import com.example.tournament.dto.MatchScoreUpdateDTO;
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
class MatchServiceTest {

    @Mock MatchRepository repo;
    @InjectMocks MatchService service;
    AutoCloseable close;

    @BeforeEach void init(){ close = MockitoAnnotations.openMocks(this); }
    @AfterEach  void tear() throws Exception { close.close(); }

    @Test
    void updateScore_refereeMismatchThrows() {
        User ref = User.builder().id(99L).build();
        Match m = Match.builder().id(7L).referee(ref).build();
        when(repo.findById(7L)).thenReturn(Optional.of(m));

        MatchScoreUpdateDTO dto = new MatchScoreUpdateDTO();
        dto.setRefereeId(42L);          // wrong ref
        dto.setScore("6-0 6-1");

        assertThatThrownBy(() -> service.updateScore(7L, dto))
                .hasMessageContaining("Only the assigned referee");
    }
}
