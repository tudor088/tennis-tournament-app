package com.example.tournament.service;

import com.example.tournament.dto.LoginDTO;
import com.example.tournament.dto.PlayerFilterDTO;
import com.example.tournament.dto.RegisterDTO;
import com.example.tournament.entity.Role;
import com.example.tournament.entity.User;
import com.example.tournament.repository.MatchRepository;
import com.example.tournament.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {
    @Autowired
    private UserRepository repo;

    @Autowired
    private MatchRepository matchRepo;

    @Autowired
    private BCryptPasswordEncoder encoder;

    public void register(RegisterDTO dto) {
        if(repo.existsByEmail(dto.getEmail())){
            throw new RuntimeException("Email already in use");
        }

        User user = User.builder()
                .username(dto.getUsername())
                .email(dto.getEmail())
                .password(encoder.encode(dto.getPassword()))
                .role(Role.valueOf(dto.getRole().toUpperCase()))
                .build();

        repo.save(user);
    }

    public Optional<User> login(LoginDTO loginDTO) {

        Optional<User> user = repo.findByEmail(loginDTO.getEmail());

        if (user.isPresent() && encoder.matches(loginDTO.getPassword(), user.get().getPassword())) {
            return user;
        }

        return Optional.empty();
    }

    public List<User> filterPlayers(PlayerFilterDTO dto) {

        // 1. keyword + tournament pre-filter
        List<User> base = repo.searchPlayers(
                dto.getKeyword(),
                dto.getTournamentId());

        // 2. (optional) incomplete-match post-filter
        if (dto.getHasIncompleteMatch() != null) {
            List<Long> busyIds = matchRepo.findPlayerIdsWithIncompleteMatches();

            if (dto.getHasIncompleteMatch()) {
                // keep ONLY players that are busy
                base.removeIf(u -> !busyIds.contains(u.getId()));
            } else {
                // drop players that are busy  ➜ keep the rest
                base.removeIf(u ->  busyIds.contains(u.getId()));
            }
        }
        return base;
    }
}
