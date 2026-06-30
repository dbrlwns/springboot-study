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