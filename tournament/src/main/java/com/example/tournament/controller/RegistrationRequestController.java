package com.example.tournament.controller;

import com.example.tournament.entity.RegistrationRequest;
import com.example.tournament.service.RegistrationRequestService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/registration-requests")
@RequiredArgsConstructor
public class RegistrationRequestController {

    private final RegistrationRequestService service;

    // ------- player submits request -------
    @PostMapping("/tournament/{tournamentId}/player/{playerId}")
    public ResponseEntity<String> create(
            @PathVariable Long tournamentId,
            @PathVariable Long playerId) {
        return ResponseEntity.ok(service.createRequest(tournamentId, playerId));
    }

    // ------- admin views pending -------
    @GetMapping
    public ResponseEntity<List<RegistrationRequest>> pending() {
        return ResponseEntity.ok(service.pending());
    }

    // ------- admin decisions -------
    @PatchMapping("/{id}/approve")
    public ResponseEntity<String> approve(@PathVariable Long id) {
        service.approve(id);
        return ResponseEntity.ok("Request approved.");
    }

    @PatchMapping("/{id}/deny")
    public ResponseEntity<String> deny(@PathVariable Long id) {
        service.deny(id);
        return ResponseEntity.ok("Request denied.");
    }
}
