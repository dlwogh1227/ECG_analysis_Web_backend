package com.simbatda.oraclespringboot.mybatis;

import com.simbatda.oraclespringboot.dto.InsertUserDTO;
import com.simbatda.oraclespringboot.vo.UserVO;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper {
    UserVO findUserByUsername(String username);

    int insertUser(InsertUserDTO user);
}
