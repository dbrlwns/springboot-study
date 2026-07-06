package me.shinsunyoung.springbootdeveloper.service;

import com.rometools.rome.feed.synd.SyndEntry;
import com.rometools.rome.feed.synd.SyndFeed;
import com.rometools.rome.feed.synd.SyndFeedImpl;
import com.rometools.rome.io.FeedException;
import com.rometools.rome.io.SyndFeedInput;
import lombok.RequiredArgsConstructor;

import lombok.Value;
import me.shinsunyoung.springbootdeveloper.config.RssProperties;
import me.shinsunyoung.springbootdeveloper.domain.News;
import me.shinsunyoung.springbootdeveloper.dto.NewsResponse;
import me.shinsunyoung.springbootdeveloper.repository.NewsRepository;
import org.springframework.stereotype.Service;

import java.io.StringReader;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Service
public class RssService {

    private final RssClient rssClient;
    private final NewsRepository repository;
    private final RssProperties rssProperties;

    // 뉴스 데이터 조회 후 반환
    public List<NewsResponse> getNews(String keyword) {
        List<News> newsList;
        if (keyword == null || keyword.isBlank()) {
            newsList = repository.findAllByOrderByPublishedAtDesc();
        } else {
            newsList = repository.findByTitleContainingOrderByPublishedAtDesc(keyword);
        }
        return newsList.stream()
                .map(news -> new NewsResponse(
                        news.getId(),
                        news.getTitle(),
                        news.getUrl(),
                        news.getPublisher(),
                        news.getPublishedAt()
                )).toList();
    }

    public int collectNews() throws Exception {

        List<News> newsList = new ArrayList<>();

        for(RssProperties.Feed feed:rssProperties.getFeeds()){
            newsList.addAll(rssToNews(feed.getUrl(), feed.getSource()));
        }

//        newsList.addAll(rssToNews(rssXml, "GOOGLE"));
        repository.saveAll(newsList);

        return newsList.size();
    }

    private List<News> rssToNews(String rssUrl, String authorship) throws Exception {
        String rssXml = rssClient.fetch(rssUrl);
        SyndFeed feed = new SyndFeedInput().build(new StringReader(rssXml));
        return feed.getEntries().stream()
                .map(entry -> entryToNews(entry, authorship))
                .filter(news -> !repository.existsByUrl(news.getUrl()))
                .toList();
    }

    private News entryToNews(SyndEntry entry, String authorship) {
        String publisher = null;
        if (entry.getSource() != null) {
            publisher = entry.getSource().getTitle();
        }

        return News.builder()
                .title(entry.getTitle())
                .url(entry.getLink())
                .publisher(publisher)
                .publishedAt(entry.getPublishedDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime())
                .fetchedAt(LocalDateTime.now())
                .authorship(authorship)
                .build();

    }
}
