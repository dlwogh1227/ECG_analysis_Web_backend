package com.simbatda.oraclespringboot.controller;

import com.simbatda.oraclespringboot.dto.InsertScoreHistoryDTO;
import com.simbatda.oraclespringboot.mybatis.PatientMapper;
import com.simbatda.oraclespringboot.vo.PatientVO;
import com.simbatda.oraclespringboot.vo.ScoreHistoryVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("api/professional")
@Log4j2
@RequiredArgsConstructor
public class ProfessionalController {

    private final PatientMapper patientMapper;
    private final RestTemplate restTemplate = new RestTemplate();

    @GetMapping("/getPatientsList")
    public ResponseEntity<List<PatientVO>> getPatientsList() {
        List<PatientVO> patients = patientMapper.selectAllPatient();
        return ResponseEntity.ok(patients);
    }

    @GetMapping("/getPatientInfo")
    public ResponseEntity<Map<String, Object>> getPatientInfo(@RequestParam Long id) {
        String flaskUrl = "http://localhost:5000/patient-info?id=" + id;
        try {
            ResponseEntity<Map> flaskResponse = restTemplate.getForEntity(flaskUrl, Map.class);
            Map<String, Object> responseBody = flaskResponse.getBody();

            if (responseBody == null) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(Map.of("error", "Flask 응답이 비어 있음"));
            }

            Map<String, Object> resultMap = flaskResponse.getBody();
            String riskClass = (String) resultMap.get("risk_class");

            patientMapper.updatePatientStatus(riskClass, id);

            return ResponseEntity.ok(responseBody);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Flask 서버 요청 실패", "detail", e.getMessage()));
        }
    }

    @GetMapping("/getScoreHistory")
    public ResponseEntity<List<ScoreHistoryVO>> getScoreHistory(@RequestParam Long id) {
        List<ScoreHistoryVO> recentScores = patientMapper.selectRecentScoreHistory(id);
        return ResponseEntity.ok(recentScores);
    }
}
