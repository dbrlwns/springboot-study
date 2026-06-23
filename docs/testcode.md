# Test

- JUnit과 AssertJ 를 사용

---

### 애너테이션
- @SpringBootTest : @SpringBootApplication이 있는 클래스를 찾고 거기 포함된 빈을 찾아 테스트용 앱 컨텐스트를 생성
- @AutoConfigureMockMvc : MockMvc를 생성하고 자동으로 구성
- @BeforeAll, @BeforeEach, @AfterAll, @AfterEach

---

#### MockMvc
어플리케이션을 서버에 배포하지 않고도 테스트용 MVC 환경을 만들어 요청 및 전송, 응답 기능을 제공하는 유틸리티, 컨트롤러 테스트 시 사용
```java
final ResultActions result = mockMvc.perform(get(url).accept(...).param(...) 또는 .content(json 내용))
```
위 처럼 테스트코드에서 요청을 보내 응답을 받고,
```java
result.andExpect(status().isOk()).andExpect(jsonPath("$[0].id").value(3)) 또는 .andExcept(content().string("value"))
```
위처럼 검증할 수 있다.

---

- Controller에서 @RequestParam()은 url 쿼리 스트링이나 form 데이터에서 값을 찾는다.
- @RequestBody는 HTTP 요청 본문 전체를 읽어 Java 객체로 바꾼다.
- Boot에서는 Jackson이 JSON을 객체로 바꿔줌

- 객체를 json으로 변환하려면 ObjectMapper의 writeValueAsString() 메서드를 사용

---
### ObjectMapper
Jackson 라이브러리에서 제공하며, 객체와 JSON 같의 변환을 처리한다.
```java
Code code = new Code(13)
objectMapper.writeValueAsString(code)
```
처럼 사용하면 JSON 형태의 문자열로 객체가 변환된다. (=객체 직렬화)
-> {'value' : 13}

--- 
### record
```java
record Code(int value){ }
```
위 처럼 사용하면 간단한 데이터 객체를 만들 수 있는데,
자동으로 생성자, getter, equals(), hashCode(), toString() 를 만들어준다.
JSON {'value' : 1}도 Spring이 new Code(1) 형태로 편하게 변환할 수 있게 된다.