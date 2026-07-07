package me.shinsunyoung.springbootdeveloper.service;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import me.shinsunyoung.springbootdeveloper.repository.NewsRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Getter
public class SearchEngineService {

    private final SearchEngine searchEngine;
    private final NewsRepository newsRepository;

    

}
