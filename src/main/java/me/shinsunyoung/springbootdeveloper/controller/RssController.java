package me.shinsunyoung.springbootdeveloper.controller;

import lombok.RequiredArgsConstructor;
import me.shinsunyoung.springbootdeveloper.repository.NewsRepository;
import me.shinsunyoung.springbootdeveloper.service.RssService;
import org.springframework.stereotype.Controller;

@RequiredArgsConstructor
@Controller
public class RssController {
    private final NewsRepository newsRepository;
    private final RssService rssService;


}
