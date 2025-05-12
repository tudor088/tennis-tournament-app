package com.example.tournament.repository;

import com.example.tournament.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);

    @Query("""
        SELECT DISTINCT u
        FROM User u
        LEFT JOIN u.tournaments t
        WHERE (:kw IS NULL OR LOWER(u.username) LIKE LOWER(CONCAT('%', :kw, '%'))
               OR LOWER(u.email) LIKE LOWER(CONCAT('%', :kw, '%')))
          AND (:tid IS NULL OR t.id = :tid)
          AND u.role = com.example.tournament.entity.Role.PLAYER
        """)
    List<User> searchPlayers(@Param("kw") String keyword,
                             @Param("tid") Long tournamentId);
}
