package me.shinsunyoung.springbootdeveloper.news;

import me.shinsunyoung.springbootdeveloper.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BookmarkRepository extends JpaRepository<Bookmark, Long> {
    boolean existsBookmarkByUserAndNews(User user, News news);   // Service에서 중복방지 사용

    List<Bookmark> findByUserOrderByCreatedAtDesc(User user);
    void deleteByUserAndNews(User user, News news);
}
