package com.example.tournament.dto;

import lombok.Data;

@Data
public class PlayerFilterDTO {
    private String keyword;          // substring on username OR email
    private Long  tournamentId;      // only players enrolled in this tournament
    private Boolean hasIncompleteMatch; // true → player still has a match with completed = false
}
