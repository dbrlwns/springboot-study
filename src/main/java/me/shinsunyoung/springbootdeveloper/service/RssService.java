package me.shinsunyoung.springbootdeveloper.service;

import com.rometools.rome.feed.synd.SyndEntry;
import com.rometools.rome.feed.synd.SyndFeed;
import com.rometools.rome.feed.synd.SyndFeedImpl;
import com.rometools.rome.io.FeedException;
import com.rometools.rome.io.SyndFeedInput;
import io.micrometer.common.util.StringUtils;
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
    private final SearchEngineService searchEngineService;

    // 뉴스 데이터 조회 후 반환
    public List<NewsResponse> getNews(String keyword, String authorship) {
        boolean hasKeyword = keyword != null && !keyword.trim().isBlank();
        boolean hasAuthorship = authorship != null && !authorship.trim().isBlank();

        List<News> newsList;

        if (hasKeyword) {
            newsList = searchEngineService.searchNews(keyword);

            if (hasAuthorship){
               newsList = newsList.stream()
                       .filter(news -> authorship.equals(news.getAuthorship()))
                       .toList();
            }
        } else if (hasAuthorship) {
            newsList = repository.findByAuthorshipContainingOrderByPublishedAtDesc(authorship);
        } else {
            newsList = repository.findAllByOrderByPublishedAtDesc();
        }
        return newsList.stream()
                .map(news -> new NewsResponse(
                        news.getId(),
                        news.getTitle(),
                        news.getUrl(),
                        news.getPublisher(),
                        news.getPublishedAt(),
                        news.getAuthorship(),
                        news.getDescription()
                )).toList();
    }

    public int collectNews() throws Exception {

        List<News> newsList = new ArrayList<>();

        for(RssProperties.Feed feed:rssProperties.getFeeds()){
            newsList.addAll(rssToNews(feed.getUrl(), feed.getSource()));
        }

//        newsList.addAll(rssToNews(rssXml, "GOOGLE"));
        List<News> savedNewsList = repository.saveAll(newsList);
        // 검색엔진에 색인 추가
        for (News news : savedNewsList) {
            searchEngineService.indexNews(news);
        }   // 색인 추가에 Id 사용을 명확하게 나타내기 위해 저장 후 반환 리스트를 사용
        // 참고로 중복 색인을 rssToNews 함수에서 필터링하므로 예방할 수 있음.

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
//                .description(entry.getDescription() == null ? "" : entry.getDescription().getValue())
                .description(extractDescription(entry))
                .build();

    }

    private String extractDescription(SyndEntry entry) {
        if (entry.getDescription() == null || entry.getDescription().getValue() == null) {
            return "";
        }
//        System.out.println(entry.getDescription().getValue().replaceAll("<[^>]*", " ").replaceAll("&nbsp;", " ").replaceAll("\\s+", " ").replaceAll(">", " ").trim());
        return entry.getDescription().getValue()
                .replaceAll("<[^>]*", " ")
                .replaceAll("&nbsp;", " ")
                .replaceAll("\\s+", " ")
                .replaceAll(">", " ")
                .trim();
    }
}
