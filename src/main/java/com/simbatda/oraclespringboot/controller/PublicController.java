package com.simbatda.oraclespringboot.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.simbatda.oraclespringboot.dto.InsertUserDTO;
import com.simbatda.oraclespringboot.dto.InsertUserRequestDTO;
import com.simbatda.oraclespringboot.dto.LoginResponseDTO;
import com.simbatda.oraclespringboot.mybatis.UserMapper;
import com.simbatda.oraclespringboot.security.CustomUserDetails;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.http.*;

import java.io.IOException;
import java.util.Map;


@Controller
@RequestMapping("/api/public")
@Log4j2
@RequiredArgsConstructor
public class PublicController {

    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final RestTemplate restTemplate = new RestTemplate();

    @GetMapping("/user/me")
    public ResponseEntity<?> me(Authentication auth, HttpServletRequest request) {
        if (auth == null) return ResponseEntity.status(401).build();

        CustomUserDetails user = (CustomUserDetails) auth.getPrincipal();
        return ResponseEntity.ok(new LoginResponseDTO(user.getUsername(), user.getAuthorities().iterator().next().getAuthority()));
    }

    @PostMapping("/insertUser")
    public ResponseEntity<?> insertUser(@RequestBody InsertUserRequestDTO insertUserRequestDTO) {
        String token = insertUserRequestDTO.getToken();
        InsertUserDTO insertUserDTO = new InsertUserDTO();
        insertUserDTO.setUsername(insertUserRequestDTO.getUsername());
        insertUserDTO.setPassword(passwordEncoder.encode(insertUserRequestDTO.getPassword()));
        if (token.equals("user")) {
            insertUserDTO.setRole("USER");
        } else if (token.equals("pro")) {
            insertUserDTO.setRole("PRO");
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "토큰이 올바르지 않습니다."));
        }
        try {
            int result = userMapper.insertUser(insertUserDTO);
            if (result == 1) {
                return ResponseEntity.ok(Map.of("message", "회원가입에 성공했습니다."));
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("message", "회원가입에 실패했습니다."));
            }
        } catch (DuplicateKeyException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("message", "이미 존재하는 아이디입니다."));
        }
    }

    @ResponseBody
    @PostMapping("upload-ecgImage-lead2only-with-checkup")
    public ResponseEntity<?> uploadEcgImageLead2onlyWithCheckup(
            @RequestParam("file") MultipartFile ecgImage,
            @RequestParam("checkup") MultipartFile checkupImage,
            @RequestParam("questionnaire") String questionnaireJson
    ) throws IOException {
        // Flask 서버 주소 (환경에 따라 수정)
        String flaskUrl = "http://localhost:5000/lead2only-with-checkup";

        // === 1. 파일 및 필드 조합
        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("file", convert(ecgImage));
        body.add("checkup", convert(checkupImage));
        body.add("questionnaire", questionnaireJson);

        // === 2. 헤더 설정
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

        // === 3. Flask로 요청 전송
        ResponseEntity<String> response = restTemplate.postForEntity(flaskUrl, requestEntity, String.class);

        ObjectMapper mapper = new ObjectMapper();
        JsonNode root = mapper.readTree(response.getBody());

        int label = root.get("label").asInt();
        double probability = root.get("probability").asDouble();
        JsonNode ecgSignal = root.get("ecg_signal"); // 이건 배열 형태
        JsonNode moreInfo = root.get("more_info"); // 이건 배열 형태
        String heatmapBase64 = root.get("heatmap").asText();
        JsonNode feature_importance = root.get("feature_importance");
        JsonNode gpt_result = root.get("gpt_result");
        JsonNode pwv_shap_prob = root.get("pwv_shap_prob");
        String pwv_shap_report = root.get("pwv_shap_report").asText();
        String pwv_shap_img_base64 = root.get("pwv_shap_img").asText();

        return ResponseEntity.ok(Map.of(
                "label", label,
                    "more_info", moreInfo,
                "probability", probability,
                "ecg_signal", ecgSignal,  // JsonNode 그대로 반환 가능
                "heatmap", heatmapBase64,
                "feature_importance", feature_importance,
                "gpt_result", gpt_result,
                "pwv_shap_prob", pwv_shap_prob,
                "pwv_shap_report", pwv_shap_report,
                "pwv_shap_img_base64", pwv_shap_img_base64
        ));
    }

    @PostMapping("upload-ecgImage-lead2only")
    public ResponseEntity<?> uploadEcgImageLead2only(
            @RequestParam("file") MultipartFile ecgImage,
            @RequestParam("questionnaire") String questionnaireJson
    ) throws IOException {
        // Flask 서버의 해당 엔드포인트 주소
        String flaskUrl = "http://localhost:5000/lead2only";  // Flask에서 정의된 엔드포인트 이름에 맞게 수정

        // === 1. 파일 및 필드 조립
        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("file", convert(ecgImage));
        body.add("questionnaire", questionnaireJson);

        // === 2. 헤더 설정
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

        // === 3. Flask로 요청 전송
        ResponseEntity<String> response = restTemplate.postForEntity(flaskUrl, requestEntity, String.class);

        ObjectMapper mapper = new ObjectMapper();
        JsonNode root = mapper.readTree(response.getBody());

        int label = root.get("label").asInt();
        double probability = root.get("probability").asDouble();
        JsonNode moreInfo = root.get("more_info"); // 이건 배열 형태
        JsonNode ecgSignal = root.get("ecg_signal"); // 이건 배열 형태
        String heatmapBase64 = root.get("heatmap").asText();
        JsonNode feature_importance = root.get("feature_importance");
        JsonNode gpt_result = root.get("gpt_result");

        return ResponseEntity.ok(Map.of(
                "label", label, //ecg normal: 0, abnormal: 1
                "more_info", moreInfo,
                "probability", probability, // 모델 예측확률
                "ecg_signal", ecgSignal,  // ecg 신호 수치화한 데이터 (2490개, column:(time, voltage))
                "heatmap", heatmapBase64, // gradcam heatmap만 뽑은거
                "feature_importance", feature_importance, // shap 결과
                "gpt_result", gpt_result // gpt 결과
        ));
    }

    private Resource convert(MultipartFile file) throws IOException {
        return new ByteArrayResource(file.getBytes()) {
            @Override
            public String getFilename() {
                return file.getOriginalFilename();  // 파일 이름 유지
            }
        };
    }

}
