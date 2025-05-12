package com.example.tournament.repository;

import com.example.tournament.entity.Match;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


import java.util.List;

public interface MatchRepository extends JpaRepository<Match, Long> {
    List<Match> findByTournamentId(Long tournamentId);

    List<Match> findByPlayer1IdOrPlayer2Id(Long player1Id, Long player2Id);

    List<Match> findByRefereeId(Long refereeId);

    List<Match> findByRefereeIdAndCompletedFalse(Long refereeId);

    List<Match> findByRefereeIdAndCompleted(Long refereeId, boolean completed);

    @Query(value = """
        SELECT DISTINCT player1_id 
        FROM matches 
        WHERE completed = b'0'          -- ← bit literal
        UNION
        SELECT DISTINCT player2_id 
        FROM matches 
        WHERE completed = b'0'
        """, nativeQuery = true)
    List<Long> findPlayerIdsWithIncompleteMatches();
}
