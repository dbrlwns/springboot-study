package me.shinsunyoung.springbootdeveloper.news;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NewsRepository extends JpaRepository<News,Long> {

    boolean existsByUrl(String url);

    Page<News> findAllByOrderByPublishedAtDesc(Pageable pageable);

    List<News> findByTitleContainingOrderByPublishedAtDesc(String title);

    // RSS 출처별 반환
    Page<News> findByAuthorshipContainingOrderByPublishedAtDesc(String authorship,
                                                                Pageable pageable);

    // Title + Authorship 같이 적용
    List<News> findByTitleContainingAndAuthorshipOrderByPublishedAtDesc(String title, String authorship);
}
