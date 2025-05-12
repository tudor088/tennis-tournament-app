package com.example.tournament.service;

import com.example.tournament.entity.*;
import com.example.tournament.repository.RegistrationRequestRepository;
import com.example.tournament.repository.TournamentRepository;
import com.example.tournament.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RegistrationRequestService {

    private final RegistrationRequestRepository repo;
    private final UserRepository userRepo;
    private final TournamentRepository tournamentRepo;
    private final EmailService mail;

    // --------------  player-side  --------------
    public String createRequest(Long tournamentId, Long playerId) {
        User player = userRepo.findById(playerId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        if (player.getRole() != Role.PLAYER)
            throw new RuntimeException("Only players can register");

        Tournament t = tournamentRepo.findById(tournamentId)
                .orElseThrow(() -> new RuntimeException("Tournament not found"));

        if (repo.findByPlayerIdAndTournamentId(playerId, tournamentId).isPresent())
            throw new RuntimeException("Request already exists");

        repo.save(RegistrationRequest.builder()
                .player(player)
                .tournament(t)
                .build());

        return "Registration request submitted (pending admin approval).";
    }

    // --------------  admin-side  --------------
    public List<RegistrationRequest> pending() {
        return repo.findByStatus(RegistrationStatus.PENDING);
    }

    @Transactional
    public void approve(Long requestId) {
        RegistrationRequest rr = repo.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Request not found"));
        if (rr.getStatus() != RegistrationStatus.PENDING)
            throw new RuntimeException("Request already processed");

        rr.setStatus(RegistrationStatus.APPROVED);
        rr.getPlayer().getTournaments().add(rr.getTournament());   // link player <-> tournament
        mail.sendRegistrationDecision(rr.getPlayer(), rr.getTournament(), true);
    }

    @Transactional
    public void deny(Long requestId) {
        RegistrationRequest rr = repo.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Request not found"));
        if (rr.getStatus() != RegistrationStatus.PENDING)
            throw new RuntimeException("Request already processed");

        rr.setStatus(RegistrationStatus.DENIED);
        mail.sendRegistrationDecision(rr.getPlayer(), rr.getTournament(), false);
    }
}
