# Search Engine

처음에는 Spring Data JPA의 `findByTitleContaining...` 방식으로 구현. 
이후 뉴스 제목과 언론사를 메모리 인덱스에 색인하고 BM25 점수로 검색 결과를 정렬하는 경량 검색엔진을 추가

---

## 1. 전체 흐름

RSS 수집
- -> News 엔티티 저장
- -> SearchEngineService.indexNews(news)
- -> SearchEngine.index(newsId, title + publisher)
- -> (word, (newsId, count)) 형태의 역색인 생성
- -> /news?keyword=... 요청
- -> SearchEngine.search(keyword)
- -> BM25 점수순 newsId 반환
- -> NewsRepository.findAllById(newsIds)
- -> 검색 점수 순서대로 NewsResponse 변환

검색엔진은 DB를 직접 대체하지 않는다. 
DB는 뉴스 원본 데이터를 저장하고, 검색엔진은 검색을 빠르게 하기 위한 인덱스와 랭킹 계산을 담당한다

---

## 2. 핵심 자료구조

SearchEngine은 두 개의 Map을 중심으로 동작한다.

```java
Map<Long, String> _documents = new HashMap<>();
Map<String, Map<Long, Integer>> _index = new HashMap<>();
```

### _documents

- (newsId, 원본 문자열) 구조

Ex:
- (1, "스프링부트 검색엔진 구현 TechNews")
- (2, "구글 뉴스 RSS 수집 Google")

### _index
- (word, (newsId, count)) 구조

예시:

"검색" -> {1=1}
"뉴스" -> {2=1}
"google" -> {2=1}
```

이 구조를 역색인이라고 한다. 검색어가 들어왔을 때 전체 문서를 모두 순회하지 않고, 해당 단어가 들어간 문서 목록을 바로 찾을 수 있다.

---

## 3. SearchEngine 메서드

### tokenize()

정규화된 문자열을 검색 토큰 목록으로 변환한다.

기본적으로 공백 기준 토큰을 만들고, 한글 검색 품질을 보완하기 위해 한글 연속 구간에는 2-gram 토큰을 추가한다.

Ex
```text
"스프링부트검색엔진"
-> "스프링부트검색엔진"
-> "스프", "프링", "링부", "부트", "트검", "검색", "색엔", "엔진"
```

이렇게 하면 공백 없이 붙어 있는 한글 제목에서도 `"검색"`, `"엔진"` 같은 부분 검색이 가능해진다.

---

### index()

문서 하나를 검색엔진에 등록한다.

```java
public void index(Long newsId, String content)
```


```text
1. _documents에 newsId와 content 저장
2. content를 tokenize()
3. 각 token에 대해 _index[token][newsId] count 증가
4. 평균 문서 길이 캐시(_avdl) 초기화
```

색인 결과:
```text
_documents
1 -> "검색엔진 구현"

_index
"검색엔진" -> {1=1}
"검색" -> {1=1}
"색엔" -> {1=1}
"엔진" -> {1=1}
"구현" -> {1=1}
```

---

### get_urls()

특정 키워드가 들어간 문서와 등장 횟수를 반환한다.

```java
public Map<Long, Integer> get_urls(String keyword)
```

Ex:
```text
get_urls("검색")
-> {1=1, 5=2}
```

이 결과는 BM25 계산에서 `freq` 값으로 사용된다.

---

### idf()

키워드의 희귀도를 계산한다.

```java
public double idf(String kw)
```

```text
N: 전체 문서 수
nKw: 해당 키워드가 등장한 문서 수
```

많은 문서에 등장하는 단어는 흔한 단어이므로 점수가 낮아지고, 적은 문서에만 등장하는 단어는 희귀하므로 점수가 높아진다.

---

### avdl()

전체 문서의 평균 길이를 계산한다.

```java
public double avdl()
```

BM25는 긴 문서에 단어가 우연히 많이 나오는 경우를 보정한다. 이를 위해 전체 평균 문서 길이를 사용한다.

문서가 추가될 때마다 평균 길이가 달라질 수 있으므로, index() 함수가 실행되면 _avdl을 null로 초기화한다.

---

### bm25()

키워드 하나에 대해 문서별 검색 점수를 계산한다.

```java
public Map<Long, Double> bm25(String kw)
```


Ex.
```text
bm25("검색")
-> {1=0.72, 5=0.49}
```

---

### search()

사용자 검색어 전체를 처리한다.

```java
public Map<Long, Double> search(String query)
```


```text
1. query를 tokenize()
2. 각 token마다 bm25(token) 실행
3. 같은 newsId의 점수는 합산
4. 최종 점수를 내림차순 정렬
```


각 토큰의 점수를 계산한 뒤 같은 뉴스 ID 기준으로 합산한다.

---

### sortByScoreDesc()

검색 결과를 점수 높은 순서로 정렬한다.

`HashMap`은 순서를 보장하지 않기 때문에, 정렬 후 `LinkedHashMap`에 다시 담아 점수순을 유지한다.

---

## 4. SearchEngineService

SearchEngineService는 Spring과 검색엔진을 연결한다.

### 앱 시작 시 기존 뉴스 색인

```java
@EventListener(ApplicationReadyEvent.class)
public void rebuildIndex()
```

Spring 앱이 실행된 뒤 DB에 저장된 모든 뉴스를 읽어 검색엔진에 다시 색인한다.

```text
ApplicationReadyEvent
-> searchEngine.clear()
-> newsRepository.findAll()
-> indexNews(news)
```

---

### indexNews()

뉴스 하나를 검색엔진에 색인한다.

```java
public void indexNews(News news)
```

내부에서는 makeSearchContent()로 검색 대상 문자열을 만든 뒤 searchEngine.index()에 전달한다.

---

### makeSearchContent()

현재 검색 대상은 뉴스 제목과 언론사다.

```java
return title + " " + publisher;
```

---

### searchNews()

검색어를 받아 점수순 뉴스 목록을 반환한다.

```text
keyword
-> searchEngine.search(keyword)
-> 점수순 newsId 목록
-> newsRepository.findAllById(newsIds)
-> Map<Long, News> 변환
-> newsId 점수순으로 News 재정렬
```

findAllById()는 전달한 ID 순서를 보장하지 않을 수 있으므로, 검색엔진이 반환한 newsIds 순서대로 다시 정렬한다.

---

## 5. RssService와의 연결

뉴스 수집 후 새로 저장된 뉴스는 바로 검색엔진에도 추가 색인한다.

```text
RSS 수집
-> repository.saveAll(newsList)
-> savedNewsList 순회
-> searchEngineService.indexNews(news)
```

검색 요청이 들어오면 `RssService.getNews()`에서 다음과 같이 분기한다.

```text
keyword 있음
-> searchEngineService.searchNews(keyword)

keyword 있음 + authorship 있음
-> searchEngineService.searchNews(keyword)
-> authorship 필터 적용

keyword 없음 + authorship 있음
-> repository.findByAuthorship...

keyword 없음 + authorship 없음
-> repository.findAllByOrderByPublishedAtDesc()
```

---

## 6. 현재 검색엔진의 장점

- DB의 단순 `LIKE` 검색이 아니라 검색 점수 기반 랭킹을 이용한다.
- `word -> newsId -> count` 역색인 구조를 직접 확인가능하다. 
- BM25 알고리즘으로 단어 빈도, 희귀도, 문서 길이를 함께 고려한다.
- 한글 2-gram 토큰화를 적용해 붙어 있는 한글 제목도 부분 검색할 수 있다.
- 검색엔진과 Spring 연결부를 `SearchEngineService`로 분리해 MVC 구조를 쉽게 적용할 수 있다.

---

## 7. 개선 사항

- 기사 HTML을 읽어 본문 텍스트 추출
- News 엔티티에 summary 필드 추가
- 검색 대상 문자열을 title + publisher + content로 확장
- 메모리 인덱스 대신 Redis 같은 캐시 저장소 사용

---

