package me.shinsunyoung.springbootdeveloper.service;

import com.rometools.rome.feed.synd.SyndEntry;
import com.rometools.rome.feed.synd.SyndFeed;
import com.rometools.rome.feed.synd.SyndFeedImpl;
import com.rometools.rome.io.FeedException;
import com.rometools.rome.io.SyndFeedInput;
import lombok.RequiredArgsConstructor;

import me.shinsunyoung.springbootdeveloper.domain.News;
import me.shinsunyoung.springbootdeveloper.dto.NewsResponse;
import me.shinsunyoung.springbootdeveloper.repository.NewsRepository;
import org.springframework.stereotype.Service;

import java.io.StringReader;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

@RequiredArgsConstructor
@Service
public class RssService {

    private final RssClient rssClient;
    private final NewsRepository repository;

    // 뉴스 데이터 조회 후 반환
    public List<NewsResponse> getNews() {
        return repository.findAllByOrderByPublishedAtDesc().stream()
                .map(news -> new NewsResponse(
                        news.getId(),
                        news.getTitle(),
                        news.getUrl(),
                        news.getPublisher(),
                        news.getPublishedAt()
                )).toList();
    }

    public int collectNews() throws Exception {
        String rssXml = rssClient.fetch();
        SyndFeed feed = new SyndFeedInput().build(new StringReader(rssXml));

        List<News> newsList = feed.getEntries().stream()
                .map(this::entryToNews)
                .filter(news -> !repository.existsByUrl(news.getUrl()))
                .toList();

        repository.saveAll(newsList);

        return newsList.size();
    }

    private News entryToNews(SyndEntry entry) {
        String publisher = null;
        if (entry.getSource() != null) {
            publisher = entry.getSource().getTitle();
        }

        return News.builder()
                .title(entry.getTitle())
                .url(entry.getLink())
                .publisher(publisher)
//                .publishedAt(LocalDateTime.ofInstant(entry.getPublishedDate().toInstant(), ZoneId.systemDefault()))
                .publishedAt(entry.getPublishedDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime())
                .fetchedAt(LocalDateTime.now())
                .build();

    }
}
