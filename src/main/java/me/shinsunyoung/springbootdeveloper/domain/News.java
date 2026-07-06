package me.shinsunyoung.springbootdeveloper.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class News {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", updatable = false)
    private Long id;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "url", length = 2048, unique = true) // SqlExceptionHelper : Value too long 방지, 중복 저장 방지
    private String url;

    @Column(name = "publisher")
    private String publisher;

    @Column(name = "publishedAt")
    private LocalDateTime publishedAt;

    @CreatedDate
    @Column(name = "fetchedAt")
    private LocalDateTime fetchedAt;

    @Column(name = "authorship")
    private String authorship;

    @Builder
    public News(String title, String url, String publisher, LocalDateTime publishedAt, LocalDateTime fetchedAt, String authorship) {
        this.title = title;
        this.url = url;
        this.publisher = publisher;
        this.publishedAt = publishedAt;
        this.fetchedAt = fetchedAt;
        this.authorship = authorship;

    }

}
