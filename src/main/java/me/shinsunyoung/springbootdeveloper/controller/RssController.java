package me.shinsunyoung.springbootdeveloper.controller;

import lombok.RequiredArgsConstructor;
import me.shinsunyoung.springbootdeveloper.repository.NewsRepository;
import me.shinsunyoung.springbootdeveloper.service.RssService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;


@RequiredArgsConstructor
@Controller
public class RssController {
    private final NewsRepository newsRepository;
    private final RssService rssService;


    @GetMapping("/news")
    public String newsList(Model model){
        model.addAttribute("newsList", newsRepository.findAll());
        return "newsList";
    }

    @PostMapping("/news/collect")
    public String collectNews() throws Exception {
        rssService.collectNews();
        return "redirect:/news";
    }
}
