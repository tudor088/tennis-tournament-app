package com.example.tournament.controller;

import com.example.tournament.entity.Match;
import com.example.tournament.entity.Role;
import com.example.tournament.entity.Tournament;
import com.example.tournament.entity.User;
import com.example.tournament.repository.MatchRepository;
import com.example.tournament.repository.UserRepository;
import com.example.tournament.service.MatchService;
import com.example.tournament.service.TournamentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.MediaType;


import java.util.List;

@RestController
@RequestMapping("/tournaments")
public class TournamentController {

    @Autowired
    private TournamentService service;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MatchService matchService;

    @PostMapping("/create")
    public ResponseEntity<Tournament> createTournament(@RequestBody Tournament t) {
        return ResponseEntity.ok(service.createTournament(t));
    }


    @GetMapping
    public ResponseEntity<List<Tournament>> getAllTournaments() {
        return ResponseEntity.ok(service.getAllTournaments());
    }

//    @PostMapping("/{tournamentId}/join/{userId}")
//    public ResponseEntity<?> joinTournament(
//            @PathVariable Long tournamentId,
//            @PathVariable Long userId) {
//        try {
//            return ResponseEntity.ok(service.registerPlayer(tournamentId, userId));
//        } catch (RuntimeException e) {
//            return ResponseEntity.badRequest().body(e.getMessage());
//        }
//    }

    @GetMapping("/{id}/players")
    public ResponseEntity<?> getPlayers(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(service.getPlayersInTournament(id));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/{id}/export-players")
    public ResponseEntity<?> exportPlayers(@PathVariable Long id) {
        try {
            Tournament tournament = service.getTournamentById(id);
            List<User> players = tournament.getPlayers();

            StringBuilder sb = new StringBuilder();
            sb.append("ID,Username,Email,Role\n");

            for (User player : players) {
                sb.append(String.format("%d,%s,%s,%s\n",
                        player.getId(),
                        player.getUsername(),
                        player.getEmail(),
                        player.getRole()));
            }

            return ResponseEntity.ok()
                    .header("Content-Disposition", "attachment; filename=players_tournament_" + id + ".csv")
                    .header("Content-Type", "text/csv")
                    .body(sb.toString());

        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/{id}/export-matches/csv")
    public ResponseEntity<?> exportMatchesCSV(@PathVariable Long id) {
        try {
            Tournament tournament = service.getTournamentById(id); // validate existence
            List<Match> matches = matchService.getByTournament(id);

            StringBuilder sb = new StringBuilder();
            sb.append("MatchID,Player1,Player2,Referee,Score,Completed\n");

            for (Match match : matches) {
                sb.append(String.format("%d,%s,%s,%s,%s,%s\n",
                        match.getId(),
                        match.getPlayer1().getUsername(),
                        match.getPlayer2().getUsername(),
                        match.getReferee().getUsername(),
                        match.getScore(),
                        match.isCompleted()));
            }

            return ResponseEntity.ok()
                    .header("Content-Disposition", "attachment; filename=matches_tournament_" + id + ".csv")
                    .header("Content-Type", "text/csv")
                    .body(sb.toString());

        } catch (RuntimeException e) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body("Tournament with ID " + id + " not found.");
        }
    }


    @GetMapping("/{id}/export-matches/txt")
    public ResponseEntity<?> exportMatchesTXT(@PathVariable Long id) {
        StringBuilder sb = new StringBuilder();
        try {
            Tournament tournament = service.getTournamentById(id); // validate existence
            List<Match> matches = matchService.getByTournament(id);

            sb.append("MATCHES FOR TOURNAMENT ID " + id + "\n\n");

            for (Match match : matches) {
                sb.append(String.format(
                        "Match #%d\n  Player1: %s\n  Player2: %s\n  Referee: %s\n  Score: %s\n  Completed: %s\n\n",
                        match.getId(),
                        match.getPlayer1().getUsername(),
                        match.getPlayer2().getUsername(),
                        match.getReferee().getUsername(),
                        match.getScore(),
                        match.isCompleted() ? "Yes" : "No"));
            }

            return ResponseEntity.ok()
                    .header("Content-Disposition", "attachment; filename=matches_tournament_" + id + ".txt")
                    .header("Content-Type", "text/plain")
                    .body(sb.toString());

        } catch (RuntimeException e) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body("Tournament with ID " + id + " not found.");
        }
    }


    @GetMapping("/{id}/export-players/txt")
    public ResponseEntity<?> exportPlayersTXT(@PathVariable Long id) {
        StringBuilder sb = new StringBuilder();
        try {
            Tournament tournament = service.getTournamentById(id); // validate existence
            List<User> players = tournament.getPlayers();

            sb.append("PLAYERS IN TOURNAMENT ID " + id + "\n\n");

            for (User player : players) {
                sb.append(String.format("ID: %d | Username: %s | Email: %s | Role: %s\n",
                        player.getId(),
                        player.getUsername(),
                        player.getEmail(),
                        player.getRole()));
            }

            return ResponseEntity.ok()
                    .header("Content-Disposition", "attachment; filename=players_tournament_" + id + ".txt")
                    .header("Content-Type", "text/plain")
                    .body(sb.toString());

        } catch (RuntimeException e) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body("Tournament with ID " + id + " not found.");
        }
    }


}
