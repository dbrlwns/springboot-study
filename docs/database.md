# ORM
객체와 데이터베이스를 연결하는 방법 -> 자바 언어로 DB를 조작할 수 있다

---

### JPA
RDB 사용 방식을 정의하는 인터페이스
자바 객체와 데이터베이스를 연결해 데이터를 관리
### Hibernate
JPA 인터페이스를 구현한 구현체 및 자바용 ORM 프레임워크
내부적으로 JDBC API를 사용

---
엔티티는 DB의 테이블과 매핑되는 객체를 말하는데, 
데이터베이스에 영향을 미치는 쿼리를 실행하는 객체다.

### 엔티티 매니저
DB와 앱 사이에서 객체(엔티티)를 생성/수정/삭제하는 역할을 한다.
엔티티 매니저 팩토리가 생성함.

스프링 부트는 앤티티 매니저 팩토리를 하나만 생성해서 관리하고 애너테이션으로 엔티티 매니저를 사용

스프링 부트는 빈을 하나만 생성해서 공유하므로 동시성 문제 발생 가능
실제로는 프록시 엔티티 매니저를 사용하고, 필요할 때 DB 트랜잭션과 관련된 실제 엔티티 매니저를 호출한다.


---
## 영속성 컨텍스트
엔티티를 관리하는 가상의 공간, 엔티티 매니저가 엔티티를 저장하는 공간

| 특징    | 개요                                            |
|-------|-----------------------------------------------|
| 1차 캐시 | 엔티티 조회 시 1차 캐시를 먼저 조회, 이때 키는 @Id가 달린 식별자      |
| 쓰기 지연 | 트랜잭션 커밋 전까지 DB에 질의문을 보내지 않고 모았다가 커밋시 한번에 쿼리 실행 |
| 변경 감지 | 1차 캐시와 현재 엔티티 값을 비교, 변경 사항을 감지해 DB에 자동으로 반영   |
| 지연 로딩 | 쿼리로 요청한 데이터를 바로 로딩하지 않고 필요할 때 쿼리를 날려 데이터 조회   |

-> 데이터베이스의 접근을 최소화해 성능을 높일 수 있다

### 엔티티의 상태
- 분리 상태
- 관리 상태
- 비영속 상태
- 삭제된 상태

---
### 스프링 데이터 JPA 
DB 사용 기능을 클래스 레벨에서 추상화하여, 인터페이스를 통해 사용

```java
public interface MemberRepository extends JpaRepository<Member, Long>{}
```
스프링 데이터 JPA를 사용해 리포지토리 역할을 하는 인터페이스를 만들어 DB 조작이 가능

테스트 시 insert-members.sql 같은 파일에 튜플을 추가하는 SQL문 작성 후
@Sql("/insert-members.sql") 어노테이션으로 해당 함수에서 독립적으로 테스트 가능

```java
public interface MemberRepository extends JpaRepository<Member, Long>{
    Optional<Member> findByName(String name);
}
```
위 처럼 JPA는 메서드 이름으로 쿼리를 작성하는 기능을 제공한다. -> 쿼리 메서드

- @Transactional 어노테이션이 포함된 메서드를 호출하면 JPA는 변경 감지로 엔티티의 필드값이 변경될 때 변경 사항이 DB에 반영된다.
엔티티가 영속 상태일 때 필드값 변경하고 트랜잭션 커밋 시, JPA는 바뀐 내용을 DB에 적용함.
- 
- @DataJpaTest 어노테이션에는 @Transactional가 들어있음.

- Member.java의 어노테이션
- @Entity : JPA가 관리하는 엔티티로 지정 -> Member 클래스와 실제 DB 테이블을 매핑, name 속성 미적용으로 member 테이블명을 가짐
- @Id : 기본키 지정
- @GeneratedValue(strategy = GenerationType.IDENTITY) : 생성 방식 결정, IDENTITY는 생성을 DB에 위임 
- @Column : DB의 컬럼과 필드를 매핑, name(컬럼 이름), nullable, unique 등 설정
- 
- @NoArgsConstructor(access = AccessLevel.PROTECTED) : 생성자 자동 생성, JPA가 엔티티를 DB에서 객체로 만들 때 필요함, 인자 없는 생성자
Hibernate가 DB에서 회원 정보를 가져올 때 1. 빈 Member 객체를 만들고 2. DB에서 읽은 name값을 객체에 넣는다 이므로
Hibernate 용으로 Member() 같은 빈 생성자가 필요해서 사용한다.
이때 빈 객체를 함부로 만들지 못하도록 protected로 숨김.

---
### Flyway : SQL을 이용해 테이블을 생성
- jpa가 엔티티를 확인해서 자동 생성하는 방식은 실행 과정에서 테이블 변경 가능성이 존재한다.
- 따라서 테이블을 명시적인 SQL파일을 사용해 생성하는 Flyway를 사용
- 연결된 DB가 있어야 하므로 PostgreSQL을 먼저 적용, Docker Compose 방식 사용
- docker compose up -d 명령어로 실행, docker compose ps로 상태 확인

---
build.gradle에 설정하고
- application.yml에 설정 후
- /src/resources/db/migration/V1__create... 에 ddl 내용을 넣고
- 기존 엔티티(ex.News)의 @Lob와 같은 내용을 TEXT로 정의했으면 이를 삭제
- Flyway : 테이블 생성과 변경을 담당
- Hibernate : 엔티티와 DB 테이블이 일치하는지 검증을 담당
- PostgreSQL : 실제 데이터 저장 DB

---
### Docker Postgresql 장점
- Postgresql 설치 없이, 독립된 실행 환경에서 일정하게 관리할 수 있음.
- Docker가 요청을 컨테이너 내부의 PostgreSQL로 전달
- volumes를 사용해 컨테이너와 데이터의 생명주기를 분리하여 컨테이너 삭제 시에도 데이터가 유지
- compose.yml만 공유하면 팀 간 유사한 환경 구성 가능

---
### 전체 실행 흐름
1. docker compose up -d를 실행하면 다음 순서로 동작합니다.
2. Docker Desktop의 Docker 엔진에 명령을 전달합니다.
3. 로컬에 postgres:16 이미지가 있는지 확인합니다.
4. 이미지가 없다면 다운로드합니다.
5. Compose 설정을 이용해 컨테이너를 생성합니다.
6. postgres-data 볼륨을 연결합니다.
7. 처음 실행이라면 archive DB와 admin 사용자를 만듭니다.
8. PostgreSQL이 컨테이너 내부 5432 포트에서 대기합니다.
9. Docker가 macOS의 5432 포트를 컨테이너 5432로 연결합니다.
10. Spring Boot가 localhost:5432/archive로 접속합니다.
11. Flyway가 migration 이력을 확인하고 필요한 SQL을 실행합니다.
12. Hibernate가 엔티티와 테이블 구조를 검증합니다.