package com.simbatda.oraclespringboot.service;

import com.simbatda.oraclespringboot.dto.InsertScoreHistoryDTO;
import com.simbatda.oraclespringboot.mybatis.PatientMapper;
import com.simbatda.oraclespringboot.mybatis.UserMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Random;

@SpringBootTest
public class CustomUserDetailsServiceTest {

    @Autowired
    private CustomUserDetailsService customUserDetailsService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private PatientMapper patientMapper;

    @Test
    public void test() {
        Random random = new Random();

        for (int patientId = 2; patientId <= 252; patientId++) {
            for (int i = 0; i < 5; i++) {
                int randomScore = random.nextInt(100) + 1; // 1~100 사이 랜덤 정수

                InsertScoreHistoryDTO dto = new InsertScoreHistoryDTO();
                dto.setPatientId((long) patientId);
                dto.setScore(randomScore);

                patientMapper.insertScoreHistory(dto);
            }
        }
    }
}
