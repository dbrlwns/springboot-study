# 1. Google News RSS 
### RSS 데이터 받아와서 하나씩 Model로 변환
- RssClient.java : Google News RSS에서 XML 데이터를 받아옴. 
- RssService.java : ROME 라이브러리를 이용해 뉴스 항목을 파싱함


---
```gradle
implementation 'com.rometools:rome:2.1.0'
```

```RssClient.java
RestClient restClient = RestClient.create();

String rssXml = restClient.get()
.uri("https://news.google.com/rss?hl=ko&gl=KR&ceid=KR:ko")
.retrieve()
.body(String.class);
```

``` RssService.java
SyndFeed feed = new SyndFeedInput()
.build(new StringReader(rssXml));

for (SyndEntry entry : feed.getEntries()) {
System.out.println("제목: " + entry.getTitle());
System.out.println("URL: " + entry.getLink());
System.out.println("발행일: " + entry.getPublishedDate());

    if (entry.getSource() != null) {
        System.out.println("언론사: " + entry.getSource().getTitle());
    }
}
```

여기까지
RssClient: RSS 요청 → String 반환
RssService: String → SyndFeed 파싱


---
``` java
public int collectNews() throws Exception {
String rssXml = rssClient.fetch();
SyndFeed feed = new SyndFeedInput().build(new StringReader(rssXml));

        List<News> newsList = feed.getEntries().stream()
                .map(this::entryToNews)
                .toList();

        repository.saveAll(newsList);

        return newsList.size();
    }
```

```java
record NewsResponse(
int savedCount,
LocalDateTime collectedAt
){}
```

RssService : SyndFeed List 생성, Repository 가져와서 리스트 삽입
RssController : ResponseEntity로 반환할 NewsResponse를 정의 후 저장된 길이와 수집시각 기록

-> 중복 저장 처리가 필요함
->
```java title="NewsRepository.java"
boolean existsByUrl(String url);
```
repository에 정의 후 사용가능

```java title="NewsService.java"
List<News> newsList = feed.getEntries().stream()
.map(this::entryToNews)
.filter(news -> !repository.existsByUrl(news.getUrl()))
.toList();
```
RssService에서 필터 파이프 라인을 추가해서 중복을 방지

stream() 함수 : 데이터를 직접 저장하지 않고, 데이터를 하나씩 흘려보내며 변환하거나 필터링
.map() : 각 SyndEntry를 News로 변환한다. this::entryToNews는 메서드 참조 문법, this.entryToNews(entry)와 같음

위의 두 함수에서 Stream<SyndEntry> -map-> Stream<News> 구조로 바뀐다.

filter()는 변환된 news 중 특정 조건을 만족하는 것들만 남김. 즉, 존재하지 않는 것들만 남긴다.

Stream 파이프라인 : stream() -> map() -> filter() -> toList()
이때, toList() 같은 최종 연산을 호출해야 전체 파이프라인이 실행하는데, 이를 지연 연산이라고 한다.
메서드 체이닝 : 메서드를 .로 연결해 호출

---
URL 분리
- GET /api/news : 저장된 뉴스 목록을 JSON으로 변환 후 응답
- POST /api/news/collect : RSS를 읽어 DB에 저장
- GET /news : 뉴스 목록 표시
- POST /news/collect : RSS 수집 후 /news redirect

추후에 JS를 사용해 비동기로 수집/갱신 가능

---
Repository에 List<News>  findAllByOrderByPublishedAtDesc(); 추가

- 조회할 때 출간순(내림차순)으로 저장된 객체를 조회, DB 배치와는 관계없음

---
Repository에 List<News> findByTitleContainingOrderByPublishedAtDesc(String title); 추가

- title에 특정 키워드가 포함된 News 찾기
- service에서 keyword 인자를 받아 조건 처리, controller에서 인자(쿼리스트링)를 받도록 설정
```html
<form th:action="@{/news}" method="get">
    <label><input type="text" name="keyword" />
```
input의 name 속성을 사용해 쿼리스트링을 사용할 수 있음

--- 
```java
public String collectNews(RedirectAttributes redirectAttributes){
    redirectAttributes.addFlashAttribute("savedCount", savedCount);
    ...
}
```
- controller에서 리다이렉트 시 전송할 쿼리스트링 설정
- 이후 리다이렉트되는 controller에서 RequestParam으로 받아줘야 함.

--- 
수동 수집 -> 자동 수집
spring Main 클래스에 @EnableScheduling 를 붙여주고
스케줄러 클래스를 하나 생성하고, @Scheduled(fixedRate=(ms단위 시간))을 가진 내부 함수 생성

---

- Service의 collectNews() 에서 생기는 예외를 Controller에서 처리
- throws Exception은 Spring에 넘겨 500 에러 페이지를 보여주지만
- try-catch 문으로 직접 처리해, 에러를 직접 처리할 수 있다.

|추후 예외 처리|
|---|
|@ControllerAdvice|
|@RestControllerAdvice|
|커스텀 예외 클래스|
|HTTP 상태 코드별 응답|
|로그 처리|

---
Spring Security 설정 후 bookmark 테이블 생성
- id
- user_id
- news_id
- created_at 

- .

### 북마크 저장
뉴스 목록에서 담기 버튼 클릭
→ /news/{newsId}/bookmark 로 POST
→ 현재 로그인 사용자 확인
→ newsId로 News 조회
→ Bookmark(user, news) 저장


---
jakarta.persistence.TransactionRequiredException: No EntityManager with actual transaction available
cannot reliably process 'remove' call 
- 삭제하려면 DB 변경 작업이니까 트랜잭션이 필요한데, 현재 메서드에 트랜잭션이 없음.
- -> 서비스 메서드에 @Transactional 을 붙여 해결가능, 레포지토리 메서드에도 붙일 수 있음.
- - 
- 저장은 문제없었는데 삭제에서만 문제가 발생 : save()는 트랜잭션을 자동으로 잡아 처리되지만, remove처리는 잡아주지 않음.

---

북마크한 기사에 대해 담기 취소 버튼 추가하기
- 로그인 상태일 시 해당 사용자가 북마크한 기사들의 Id를 가져와 해당 ID의 뉴스들만 수정
- Service에서 북마크 id 반환, controller에서 ID들을 View로 전달

--- 
HADA RSS 도 추가하기
- application.yml 파일에 rss.feed 하위에 2개 항목(google, hada)로 분류
- News.java 도메인에 authorship 필드 추가
- RssService.java의 News Builder 부분 수정 및 .map()함수 파라미터 수정
- .
- .
- RssService의 newsList에 .add()로 추가하려 했으나 .toList();에 @Unmodifiable가 있어 수정불가
- Service에서 Rss를 수집해서 리스트로 반환하는 함수를 rssToList()로 분리
- Client에서 application.yml의 rss.feed 리스트를 넘기면 Service에서 feed[0].url or feed[1].url 로 사용이 가능한지 확인
- .
- .
- Client에서 fetch() 함수에 파라미터로 rssUrl을 받아 사용하면
- Service에서 url+authorship 데이터를 전달해서 파싱이 가능 
- 음.. URL이 1개였을 때는 @Value("${rss.url)") 처럼 가져와 변수 사용이 가능했지만
- 2개 이상은 확장성을 따져 리스트 안에 있는 객체를 읽는 클래스를 생성하는 것이 나음.
- .
- .
- @ConfigurationProperties(prefix = "rss") 를 가지는 RssProperties 클래스 생성
- 리스트를 가지고, 리스트의 요소 타입은 static으로 만들 내부 클래스 Feed를 사용

---
페이지에서 RSS 링크별 표시(GOOGLE, HADA)
- Repository에 전체 조회, 타이틀 조회, 출처 조회, 타이틀/출처 조회 메서드 추가
- Controller에서 인자로 타이틀과 출처를 함께 받아 넘겨주고
- Service에서 인자 유무에 따른 분기처리를 진행
- .
- .
?authorship=GOOGLE 페이지에서 북마크 저장시 전체 조회 페이지로 강제이동되는 문제 수정
- RedirectAttributes는 원래 페이지를 넘겨주지 않구나
- Referer 을 사용하면 편하게 문제 해결이 가능하다.


---
### SearchEngine에 Rss의 Description 내용도 역색인
- getDescription()에서 null이면 .getValue()에서도 null이 반환됨에 주의 (NPE 가능성)
- 정규표현식 사용으로 불필요한 내용 제거
---
