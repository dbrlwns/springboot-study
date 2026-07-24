package me.shinsunyoung.springbootdeveloper.config;

import lombok.RequiredArgsConstructor;
import me.shinsunyoung.springbootdeveloper.service.UserDetailService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;


@RequiredArgsConstructor
@Configuration
public class WebSecurityConfig {

    private final UserDetailService userService;

    @Bean       // 스프링 시큐리티 기능 비활성화
    public WebSecurityCustomizer configure() {
        return (web) -> web.ignoring()
                .requestMatchers("/static/**");
    }

    @Bean   // 특정 HTTP 요청에 웹 기반 보안 구성
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
                .authorizeHttpRequests(auth -> auth // 인증, 인가
                        .requestMatchers("/", "/login", "/signup", "/user", "/news").permitAll()
                        .anyRequest().authenticated()
                )
                .formLogin(form -> form // 폼 기반 로그인 설정
                        .loginPage("/login")
                        .defaultSuccessUrl("/news", true)
                )
                .logout(logout -> {
                    logout
//                                    .logoutSuccessUrl("/login")
                            .logoutSuccessHandler((request, response, authentication) -> {
                                String referer = request.getHeader("referer");
                                if (referer == null || referer.isBlank()) {
                                    response.sendRedirect("/login");    // fallback
                                    return;
                                }
                                response.sendRedirect(referer);
                            })
                                    .invalidateHttpSession(true);
                        }
                )
                .csrf(AbstractHttpConfigurer::disable)   // csrf 비황성화
                .build();
    }

    @Bean   // 인증 관리자 설정
    public AuthenticationManager authenticationManager(HttpSecurity http,
                                                       BCryptPasswordEncoder bCryptPasswordEncoder, UserDetailService userDetailService) throws Exception {
        DaoAuthenticationProvider authProvicer = new DaoAuthenticationProvider();
        authProvicer.setUserDetailsService(userService);    // 사용자 정보 서비스 설정
        authProvicer.setPasswordEncoder(bCryptPasswordEncoder);
        return new ProviderManager(authProvicer);
    }

    @Bean   // 패스워드 인코더를 빈으로 등록
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }
}