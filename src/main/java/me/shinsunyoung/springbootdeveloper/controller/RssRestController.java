package me.shinsunyoung.springbootdeveloper.controller;

import com.rometools.rome.io.FeedException;
import lombok.RequiredArgsConstructor;
import me.shinsunyoung.springbootdeveloper.dto.NewsResponse;
import me.shinsunyoung.springbootdeveloper.service.RssService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;

@RequiredArgsConstructor
@RestController
public class RssRestController {

    private final RssService rssService;

    @GetMapping("/news")
    public ResponseEntity<List<NewsResponse>> getNews() throws FeedException {
        List<NewsResponse> newsList = rssService.getNews();

        return ResponseEntity.status(HttpStatus.OK).body(newsList);
    }

    @PostMapping("/news/collect")
    public ResponseEntity<NewsCollectResponse> collectNews() throws FeedException {
        int statusCode;
        try{
            statusCode = rssService.collectNews();
        } catch (Exception e){
            throw new RuntimeException(e);
        }
        return ResponseEntity.status(HttpStatus.OK).body(new NewsCollectResponse(statusCode, LocalDateTime.now()));
    }
}

record NewsCollectResponse(int statusCode, LocalDateTime date) {
}
