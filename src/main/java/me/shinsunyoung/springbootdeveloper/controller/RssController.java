package me.shinsunyoung.springbootdeveloper.controller;

import com.rometools.rome.io.FeedException;
import lombok.RequiredArgsConstructor;
import me.shinsunyoung.springbootdeveloper.repository.NewsRepository;
import me.shinsunyoung.springbootdeveloper.service.RssService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;


@RequiredArgsConstructor
@Controller
public class RssController {
//    private final NewsRepository newsRepository; // Controller <-> Service <-> Repository
    private final RssService rssService;


    @GetMapping("/news")
    public String newsList(@RequestParam(required = false) String keyword, Model model){
        model.addAttribute("newsList", rssService.getNews(keyword));
        return "news";
    }

    @PostMapping("/news/collect")
    public String collectNews() throws Exception {
        rssService.collectNews();
        return "redirect:/news";
    }
}
