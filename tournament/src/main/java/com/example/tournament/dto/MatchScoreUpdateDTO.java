package com.example.tournament.dto;

import lombok.Data;

@Data
public class MatchScoreUpdateDTO {
    private Long refereeId;
    private String score;
    private boolean completed;
}
