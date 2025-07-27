package com.simbatda.oraclespringboot.config;

import com.simbatda.oraclespringboot.service.CustomUserDetailsService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable) // CSRF는 API만 사용하는 경우 꺼도 됨
                .formLogin(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/", "/index.html",
                                "/assets/**", "/images/**", "/fonts/**",
                                "/*.js", "/*.css", "/*.json", "/*.png", "/*.svg"
                        ).permitAll()  // React 정적 리소스 허용
                        .requestMatchers("/api/login").permitAll()
                        .requestMatchers("/api/logout").permitAll()
                        .requestMatchers("/api/public/**").permitAll()  // 인증 없이 접근 가능한 API
                        .requestMatchers("/api/professional/**").hasRole("professional")  // ADMIN 권한 필요
                        .requestMatchers("/api/user/**").authenticated()  // 로그인만 돼 있으면 가능
                        .anyRequest().denyAll() // 나머지는 모두 차단
                )
                .logout(logout -> logout
                        .logoutUrl("/api/logout") // 로그아웃 경로 변경
                        .logoutSuccessHandler((request, response, authentication) -> {
                            response.setStatus(HttpServletResponse.SC_OK);
                        })
                        .invalidateHttpSession(true)
                        .deleteCookies("JSESSIONID")
                )
                .sessionManagement(sess -> sess
                        .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED) // 세션 기반 인증 유지
                );
        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
