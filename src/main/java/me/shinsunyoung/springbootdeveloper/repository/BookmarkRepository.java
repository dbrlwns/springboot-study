package me.shinsunyoung.springbootdeveloper.repository;

import me.shinsunyoung.springbootdeveloper.domain.Bookmark;
import me.shinsunyoung.springbootdeveloper.domain.News;
import me.shinsunyoung.springbootdeveloper.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BookmarkRepository extends JpaRepository<Bookmark, Long> {
    boolean existsBookmarkByUserAndNews(User user, News news);   // Service에서 중복방지 사용

    List<Bookmark> findByUserOrderByCreatedAtDesc(User user);
}
