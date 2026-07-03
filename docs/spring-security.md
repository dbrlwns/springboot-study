# Spring Security
- 스프링에서 애플리케이션 보안을 담당하는 스프링 프레임워크

### 필터 기반으로 동작한다.
| SecurityFilterChain                      |
|------------------------------------------|
| SecurityContextPersistenceFilter         |
| LogoutFilter                             |
| **UsernamePasswordAuthenticationFilter** |
| DefaultloginPageGeneratingFilter         |
| BasicAuthenticationFilter                |
| ...                                      |
| ExceptionTranslationFilter               |
| **FilterSecurityInterceptor**            |


동작 흐름
- 사용자가 폼에 ID/PW 입력 시 HTTPServletRequest가 전달되고, AuthenticationFilter가 유효성 검사
- 실제 구현체인 UsernamePasswordAuthenticationToken을 만들어 넘긴다.
- 인증용 객체 UsernamePasswordAuthenticationToken을 AuthenticationManager로 보냄
- UsernamePasswordAuthenticationToken을 AuthenticationProvider로 보냄
- 사용자 아이디를 UserDetailService로 보내고, ID에 맞는 UserDetails 객체를 AuthenticationProvicer에게 전달
- DB에 있는 사용자 정보를 UserRepository로 조회함
- 입력 정보와 UserDetails 정보를 비교해 인증 처리
- 
- 인증 완료시, SecurityContextHolder에 Authentication을 저장하고,
- 성공 여부에 따라 AuthenticationSeccessHandler 또는 AuthenticationFailureHandler 핸들러를 실행


---

## 실구현
- UserDetails를 상속받은 User 클래스 생성, (Id, Email, Password)
- JpaRepository를 상속받는 UserRepository 인터페이스 생성
이때 함수명을 Optional<User> findByEmail() 또는 findByNameAndAge() 또는 FindByAgeLessThan() 처럼 
정의하면 스프링 데이터 JPA가 메서드명 분석 후 자동으로 쿼리를 생성해준다.
- 
- 로그인 시 사용자 정보를 가져오는 UserDetailService 클래스 생성(UserDetailService 인터페이스 상속)
- 여기까지 인증을 위한 도메인(User.java), 레포지토리, 서비스가 완성됨.
- 실제 인증 처리를 하는 WebSecurityConfig.java 작성


---
## 회원가입
- 회원가입 서비스 메서드 작성 후 컨트롤러 작성
- AddUserRequest Dto 클래스 생성, UserService 작성
- UserApiController과 UserController(UserViewController) 작성
- 
- login.html, signup.html 작성
- UserApiController에 로그아웃 메서드 추가, view에 로그아웃 버튼 추가

회원 가입 후 h2-console에 확인 시

| ID | EMAIL             | PASSWORD                                                     |
|----|-------------------|--------------------------------------------------------------|
| 1  | jkk4743@gmail.com | $2a$10$dpZdv68PtecByZtLFS5SneuP/31.6Jvil7zM1S0bR1R7LeX7NuN5W |

--- 
- 로그인 후에는 SecurityContext 안에 현재 사용자 정보가 들어간다.
- Thymeleaf에서는 #authentication 또는 sec:authentication으로 꺼내 볼 수 있음.

---
