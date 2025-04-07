package com.example.tournament.service;

import com.example.tournament.entity.Role;
import com.example.tournament.entity.Tournament;
import com.example.tournament.entity.User;
import com.example.tournament.repository.TournamentRepository;
import com.example.tournament.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TournamentService {

    @Autowired
    private TournamentRepository repository;

    @Autowired
    private UserRepository userRepository;

    public Tournament createTournament(Tournament t) {
        return repository.save(t);
    }

    public List<Tournament> getAllTournaments() {
        return repository.findAll();
    }

    public String registerPlayer(Long tournamentId, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (user.getRole() != Role.PLAYER) {
            throw new RuntimeException("Only players can register for tournaments");
        }

        Tournament tournament = repository.findById(tournamentId)
                .orElseThrow(() -> new RuntimeException("Tournament not found"));

        if (user.getTournaments().contains(tournament)) {
            throw new RuntimeException("Player is already registered in this tournament");
        }

        user.getTournaments().add(tournament);
        userRepository.save(user);

        return "Player " + user.getUsername() + " joined " + tournament.getName();
    }

    public List<User> getPlayersInTournament(Long tournamentId) {
        Tournament tournament = repository.findById(tournamentId)
                .orElseThrow(() -> new RuntimeException("Tournament not found"));

        return tournament.getPlayers();
    }

    public Tournament getTournamentById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Tournament not found"));
    }
}
