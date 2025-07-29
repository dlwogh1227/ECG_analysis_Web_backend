package com.simbatda.oraclespringboot.vo;

import lombok.Data;

@Data
public class ScoreHistoryVO {
    private Long id; // 생략 가능 (IDENTITY 자동 삽입이면)
    private Long patientId;
    private Integer score;
    private java.sql.Timestamp timestamp; // 생략 가능 (DEFAULT SYSTIMESTAMP)
}
