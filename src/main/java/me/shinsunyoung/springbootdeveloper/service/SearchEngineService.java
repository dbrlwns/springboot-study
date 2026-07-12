package me.shinsunyoung.springbootdeveloper.service;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import me.shinsunyoung.springbootdeveloper.domain.News;
import me.shinsunyoung.springbootdeveloper.repository.NewsRepository;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Getter
public class SearchEngineService {

    private final SearchEngine searchEngine;
    private final NewsRepository newsRepository;


    @EventListener(ApplicationReadyEvent.class) // Spring 앱 실행 후 자동 실행
    public void rebuildIndex(){

        searchEngine.clear();

        List<News> newsList = newsRepository.findAll();

        for (News news : newsList) {
            indexNews(news);
        }
    }

    public void indexNews(News news){
        String content = makeSearchContent(news);
        searchEngine.index(news.getId(), content);
        System.out.println("index id : " +  news.getId() + "\n content : " + content);
    }

    public String makeSearchContent(News news){
        String title = news.getTitle() == null ? "" : news.getTitle();
        String publisher = news.getPublisher() == null ? "" : news.getPublisher();
        String content = news.getDescription() == null ? "" : news.getDescription();
        return title + " " + publisher + " " + content;
    }

    public List<Long> search(String keyword){
        return searchEngine.search(keyword)
                .keySet()   // 중복제거
                .stream()
                .toList();
    }

    // search() 실행 시 newsId를 가진 리스트 반환 -> News 객체가 담긴 리스트로 변환
    public List<News> searchNews(String keyword){
        List<Long> newsIds = search(keyword);
        List<News> newsList = newsRepository.findAllById(newsIds);
        searchEngine.printEngineIndex(keyword);

        Map<Long, News> newsMap = newsList.stream()
                .collect(Collectors.toMap(News::getId, news -> news));

        return newsIds.stream()
                .map(newsMap::get)
                .filter(Objects::nonNull)
                .toList();
    }

}
