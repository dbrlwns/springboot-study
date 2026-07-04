package me.shinsunyoung.springbootdeveloper.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import me.shinsunyoung.springbootdeveloper.domain.Bookmark;
import me.shinsunyoung.springbootdeveloper.domain.News;
import me.shinsunyoung.springbootdeveloper.domain.User;
import me.shinsunyoung.springbootdeveloper.dto.NewsResponse;
import me.shinsunyoung.springbootdeveloper.repository.BookmarkRepository;
import me.shinsunyoung.springbootdeveloper.repository.NewsRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BookmarkService {

    private final BookmarkRepository bookmarkRepository;
    private final NewsRepository newsRepository;

    public void addBookmark(User user, Long newsId){
        News news = newsRepository.findById(newsId)
                .orElseThrow(() -> new IllegalArgumentException("News not found"));

        // 중복방지 처리
        if(bookmarkRepository.existsBookmarkByUserAndNews(user, news)){
            return;
        }

        Bookmark bookmark = Bookmark.builder()
                .user(user)
                .news(news)
                .build();

        bookmarkRepository.save(bookmark);
    }

    public List<NewsResponse> getMyBookmarkedNews(User user){
        return bookmarkRepository.findByUserOrderByCreatedAtDesc(user)
                .stream()   // Stream<Bookmark>
                .map(bookmark -> bookmark.getNews())    // Stream<News>
                .map(news -> new NewsResponse(      // Stream<NewsResponse>
                        news.getId(),
                        news.getTitle(),
                        news.getUrl(),
                        news.getPublisher(),
                        news.getPublishedAt()
                ))
                .toList();
    }

    @Transactional
    public void deleteBookmark(User user, Long newsId){
        News news = newsRepository.findById(newsId)
                .orElseThrow(() -> new IllegalArgumentException("News not found"));

        bookmarkRepository.deleteByUserAndNews(user, news);
    }

    public List<Long> getBookmarkedNewsIds(User user){
        return bookmarkRepository.findByUserOrderByCreatedAtDesc(user)
                .stream()
                .map(bookmark -> bookmark.getNews().getId())
                .toList();
    }

}
