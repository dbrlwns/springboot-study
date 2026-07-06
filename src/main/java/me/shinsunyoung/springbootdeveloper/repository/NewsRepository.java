package me.shinsunyoung.springbootdeveloper.repository;

import me.shinsunyoung.springbootdeveloper.domain.News;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NewsRepository extends JpaRepository<News,Long> {

    boolean existsByUrl(String url);

    List<News>  findAllByOrderByPublishedAtDesc();

    List<News> findByTitleContainingOrderByPublishedAtDesc(String title);

    // RSS 출처별 반환
    List<News> findByAuthorshipContainingOrderByPublishedAtDesc(String authorship);

    // Title + Authorship 같이 적용
    List<News> findByTitleContainingAndAuthorshipOrderByPublishedAtDesc(String title, String authorship);
}
