package me.shinsunyoung.springbootdeveloper.controller;

import lombok.RequiredArgsConstructor;
import me.shinsunyoung.springbootdeveloper.dto.NewsResponse;
import me.shinsunyoung.springbootdeveloper.service.RssService;
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
    public ResponseEntity<List<NewsResponse>> getNews(@RequestParam(required = false) String keyword, @RequestParam(required = false) Integer page, @RequestParam(required = false) String authorship) {
        List<NewsResponse> newsList = rssService.getNews(keyword, authorship);

        return ResponseEntity.status(HttpStatus.OK).body(newsList);
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

