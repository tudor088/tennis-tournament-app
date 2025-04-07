package com.example.tournament.service;

import com.example.tournament.dto.LoginDTO;
import com.example.tournament.dto.RegisterDTO;
import com.example.tournament.entity.Role;
import com.example.tournament.entity.User;
import com.example.tournament.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {
    @Autowired
    private UserRepository repo;

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

}
