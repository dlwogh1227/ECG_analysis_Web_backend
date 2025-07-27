package com.simbatda.oraclespringboot.vo;

import lombok.Data;

@Data
public class UserVO {
    private Long id;
    private String username;
    private String password;
    private String role;
    private String enabled;
}
