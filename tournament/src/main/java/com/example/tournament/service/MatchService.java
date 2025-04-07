package com.example.tournament.service;

import com.example.tournament.dto.MatchScoreUpdateDTO;
import com.example.tournament.entity.Match;
import com.example.tournament.repository.MatchRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class MatchService {

    @Autowired
    private MatchRepository repo;

    public Match createMatch(Match match) {
        return repo.save(match);
    }

    public List<Match> getAllMatches() {
        return repo.findAll();
    }

    public List<Match> getByTournament(Long tournamentId) {
        return repo.findByTournamentId(tournamentId);
    }

    public List<Match> getByPlayer(Long playerId) {
        return repo.findByPlayer1IdOrPlayer2Id(playerId, playerId);
    }

    public List<Match> getByReferee(Long refereeId) {
        return repo.findByRefereeId(refereeId);
    }

    public Match updateScore(Long matchId, MatchScoreUpdateDTO dto) {
        Match match = repo.findById(matchId)
                .orElseThrow(() -> new RuntimeException("Match not found"));

        if (!match.getReferee().getId().equals(dto.getRefereeId())) {
            throw new RuntimeException("Only the assigned referee can update this match");
        }

        match.setScore(dto.getScore());
        match.setCompleted(dto.isCompleted());
        return repo.save(match);
    }

    public List<Match> getMatchesForPlayer(Long playerId) {
        return repo.findAll().stream()
                .filter(m -> m.getPlayer1().getId().equals(playerId) || m.getPlayer2().getId().equals(playerId))
                .collect(Collectors.toList());
    }

    public Match updateScore(Long matchId, Long refereeId, String newScore) {
        Match match = repo.findById(matchId)
                .orElseThrow(() -> new RuntimeException("Match not found"));

        if (!match.getReferee().getId().equals(refereeId)) {
            throw new RuntimeException("You are not authorized to update this match");
        }

        match.setScore(newScore);
        match.setCompleted(true); // Optional: auto-mark as completed
        return repo.save(match);
    }

    public List<Match> getIncompleteMatchesForReferee(Long refereeId) {
        return repo.findByRefereeIdAndCompletedFalse(refereeId);
    }

    public List<Match> getCompletedMatchesForReferee(Long refereeId) {
        return repo.findByRefereeIdAndCompleted(refereeId, true);
    }
}
