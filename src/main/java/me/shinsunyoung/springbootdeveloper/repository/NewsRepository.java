package me.shinsunyoung.springbootdeveloper.repository;

import me.shinsunyoung.springbootdeveloper.domain.News;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NewsRepository extends JpaRepository<News,Long> {

    boolean existsByUrl(String url);

    List<News>  findAllByOrderByPublishedAtDesc();

    List<News> findByTitleContainingOrderByPublishedAtDesc(String title);
}
