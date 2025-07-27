package com.simbatda.oraclespringboot.service;

import com.simbatda.oraclespringboot.mybatis.UserMapper;
import com.simbatda.oraclespringboot.security.CustomUserDetails;
import com.simbatda.oraclespringboot.vo.UserVO;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserMapper userMapper;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserVO user = userMapper.findUserByUsername(username);

        if (user == null || !"Y".equalsIgnoreCase(user.getEnabled())) {
            throw new UsernameNotFoundException("사용자 없음 또는 비활성화된 계정");
        }

        return new CustomUserDetails(user);
    }
}
