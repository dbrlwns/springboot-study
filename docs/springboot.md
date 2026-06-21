# Spring Concept
### IoC (Inversion of Control)
- 클래스 A에서 클래스 B를 직접 new로 생성하지 않고 클래스 A에서 private B b; 로 정의해 코드에서 객체를 생성하지 않고, 받아온 객체를 할당하는 방식

### DI (Dependency Injection)
- 클래스 A에서 객체를 B b; 처럼 선언하고 바로 윗줄에 @Autowired 애너테이션을 적용하면 스프링 컨테이너에 있는 빈을 주입한다.
- 스프링 컨테이너에서 생성된 객체 B를 클래스 A에 전달(주입)하여 사용한다. 이를 합쳐서 IoC/DI 라고 생각하면 된다.

### AOP (Aspect Oriented Programming)
- 핵심 관점과 부가 관점으로 나눠 관심 기준으로 모듈화
- 부가 관점 코드를 핵심 관점 코드에서 분리하면 변경/확장에 유리하다.

### PSA (Portable Service Abstraction)
- 스프링에서 제공하는 다양한 기술을 추상화하여 일관화된 사용을 가능하게 해주는 인터페이스

--- 

## Spring Container
- 컨테이너에서 Bean이 생성되고 관리된다.
- Bean은 스프링에서 제공해주는 객체

### @SpringBootApplication 애너테이션 내용
- @SpringBootConfiguration : 스프링 부트 관련 설정
- @ComponentScan : 사용자가 등록한 빈을 읽고 등록함.

| 애너테이션명 | 설명 |
| --- | --- |
| @Configuration | 설정 파일 등록 |
| @Repository | ORM 매핑 |
| @Controller, @RestController | 라우터(HTTP요청과 메서드를 연결) |
| @Service | 비즈니스 로직 |

---

# Spring Boot Structure


---

##### JAVA 17 추가점
- 텍스트 블록 ("""로 여러 줄 처리가능)
- formatted 메서드
- 레코드 (record Item(String name){} 정의 후 인스턴스 생성 가능)
- 패턴 매칭 (if(o instanceof Integer i){})
- 자료형에 맞는 case 처리 (switch 문)
- Servlet, JPA의 namespace가 jakarta로 변경

