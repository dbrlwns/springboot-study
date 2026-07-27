package me.shinsunyoung.springbootdeveloper.news;


import java.time.LocalDateTime;

public record NewsResponse(
        Long id,
        String title,
        String url,
        String publisher,
        LocalDateTime publishedAt,
        String authorship,
        String description
){}
