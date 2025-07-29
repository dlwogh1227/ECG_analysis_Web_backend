package com.simbatda.oraclespringboot.mybatis;

import com.simbatda.oraclespringboot.dto.InsertScoreHistoryDTO;
import com.simbatda.oraclespringboot.vo.PatientVO;
import com.simbatda.oraclespringboot.vo.ScoreHistoryVO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface PatientMapper {
    void insertPatient(PatientVO patientVO);

    List<PatientVO> selectAllPatient();

    void updatePatientStatus(String status, long id);

    void insertScoreHistory(InsertScoreHistoryDTO insertScoreHistoryDTO);

    List<ScoreHistoryVO> selectRecentScoreHistory(Long patientId);
}
