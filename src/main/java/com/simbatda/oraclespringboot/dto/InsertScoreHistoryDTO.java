package com.simbatda.oraclespringboot.dto;

import lombok.Data;

@Data
public class InsertScoreHistoryDTO {
    private Long patientId;
    private Integer score;
}
