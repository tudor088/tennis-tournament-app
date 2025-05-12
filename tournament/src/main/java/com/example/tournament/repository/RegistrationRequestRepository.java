package com.example.tournament.repository;

import com.example.tournament.entity.RegistrationRequest;
import com.example.tournament.entity.RegistrationStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RegistrationRequestRepository extends JpaRepository<RegistrationRequest, Long> {
    List<RegistrationRequest> findByStatus(RegistrationStatus status);
    Optional<RegistrationRequest> findByPlayerIdAndTournamentId(Long playerId, Long tournamentId);
}
