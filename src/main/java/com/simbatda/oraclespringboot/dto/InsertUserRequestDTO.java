package com.simbatda.oraclespringboot.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class InsertUserRequestDTO {
    private String username;
    private String password;
    private String token;
}
