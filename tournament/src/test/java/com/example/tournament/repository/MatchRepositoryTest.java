package com.example.tournament.repository;

import com.example.tournament.entity.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.TestPropertySource;

import java.util.List;

import static org.assertj.core.api.Assertions.*;

@DataJpaTest
@TestPropertySource(properties = {
        // 1) Use the correct dialect so no ENGINE / ENUM etc. is generated
        "spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.H2Dialect",
        // 2) (Optional) keep user-friendly logs
        "spring.jpa.show-sql=false"
})
class MatchRepositoryTest {

    @Autowired MatchRepository repo;
    @Autowired UserRepository userRepo;
    @Autowired TournamentRepository tourRepo;

    @Test
    void findPlayerIdsWithIncompleteMatches_returnsCorrectIds() {
        // --- arrange minimal data in H2 ---
        User p1 = userRepo.save(User.builder().username("A").email("a@x").password("p").role(Role.PLAYER).build());
        User p2 = userRepo.save(User.builder().username("B").email("b@x").password("p").role(Role.PLAYER).build());
        User referee = userRepo.save(User.builder().username("R").email("r@x").password("p").role(Role.REFEREE).build());
        Tournament t = tourRepo.save(Tournament.builder().name("Test").build());

        // one unfinished + one finished match
        repo.save(Match.builder()
                .player1(p1).player2(p2).referee(referee)
                .tournament(t).completed(false).build());
        repo.save(Match.builder()
                .player1(p1).player2(p2).referee(referee)
                .tournament(t).score("6-0 6-0").completed(true).build());

        // --- act ---
        List<Long> ids = repo.findPlayerIdsWithIncompleteMatches();

        // --- assert ---
        assertThat(ids).containsExactlyInAnyOrder(p1.getId(), p2.getId());
    }
}
