package com.example.tournament.controller;

import com.example.tournament.dto.MatchScoreUpdateDTO;
import com.example.tournament.dto.ScoreUpdateDTO;
import com.example.tournament.entity.Match;
import com.example.tournament.service.MatchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/matches")
public class MatchController {

    @Autowired
    private MatchService service;

    @PostMapping("/create")
    public ResponseEntity<Match> create(@RequestBody Match m) {
        return ResponseEntity.ok(service.createMatch(m));
    }

    @GetMapping
    public ResponseEntity<List<Match>> getAll() {
        return ResponseEntity.ok(service.getAllMatches());
    }

    @GetMapping("/tournament/{id}")
    public ResponseEntity<List<Match>> byTournament(@PathVariable Long id) {
        return ResponseEntity.ok(service.getByTournament(id));
    }

    @GetMapping("/player/{id}")
    public ResponseEntity<List<Match>> byPlayer(@PathVariable Long id) {
        List<Match> matches = service.getMatchesForPlayer(id);
        return ResponseEntity.ok(matches);
    }


    @GetMapping("/referee/{id}")
    public ResponseEntity<List<Match>> byReferee(@PathVariable Long id) {
        return ResponseEntity.ok(service.getByReferee(id));
    }

    @PatchMapping("/{matchId}/score")
    public ResponseEntity<?> updateScore(
            @PathVariable Long matchId,
            @RequestBody MatchScoreUpdateDTO dto) {
        try {
            return ResponseEntity.ok(service.updateScore(matchId, dto));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/referee/{id}/incomplete")
    public ResponseEntity<List<Match>> getIncompleteByReferee(@PathVariable Long id) {
        return ResponseEntity.ok(service.getIncompleteMatchesForReferee(id));
    }

    @GetMapping("/referee/{id}/completed")
    public ResponseEntity<List<Match>> getCompletedByReferee(@PathVariable Long id) {
        return ResponseEntity.ok(service.getCompletedMatchesForReferee(id));
    }
}
