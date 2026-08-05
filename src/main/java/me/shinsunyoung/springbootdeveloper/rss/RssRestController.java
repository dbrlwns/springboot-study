package me.shinsunyoung.springbootdeveloper.rss;

import lombok.RequiredArgsConstructor;
import me.shinsunyoung.springbootdeveloper.news.NewsResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;

@RequiredArgsConstructor
@RestController
public class RssRestController {

    private final RssService rssService;

    @GetMapping("/api/news")
    public ResponseEntity<Page<NewsResponse>> getNews(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String authorship,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        int safePage = Math.max(page, 0);
        int safeSize = Math.min(Math.max(size, 1), 100);

//        List<NewsResponse> newsList = rssService.getNews(keyword, authorship, );
        Page<NewsResponse> newsPage = rssService.getNews(
                keyword,
                authorship,
                PageRequest.of(safePage, safeSize)
        );

//        return ResponseEntity.status(HttpStatus.OK).body(newsPage);
        return ResponseEntity.ok(newsPage);
    }

    @PostMapping("/api/news/collect")
    public ResponseEntity<NewsCollectResponse> collectNews(){
        int savedCode;
        try {
            savedCode = rssService.collectNews();
        } catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new NewsCollectResponse(0, LocalDateTime.now(), "뉴스 수집 실패"));
        }
        return ResponseEntity.status(HttpStatus.OK).body(new NewsCollectResponse(savedCode, LocalDateTime.now(), null));
    }


    public record NewsCollectResponse(int savedCount, LocalDateTime date, String errorMessage) {}
}
