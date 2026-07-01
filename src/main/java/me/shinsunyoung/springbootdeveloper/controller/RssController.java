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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;


@RequiredArgsConstructor
@Controller
public class RssController {
//    private final NewsRepository newsRepository; // Controller <-> Service <-> Repository
    private final RssService rssService;


    @GetMapping("/news")
    public String newsList(@RequestParam(required = false) String keyword,
                           @RequestParam(required = false) Integer savedCount,
                           Model model){
        model.addAttribute("newsList", rssService.getNews(keyword));
        model.addAttribute("keyword", keyword);
        model.addAttribute("savedCount", savedCount);
        return "news";
    }

    @PostMapping("/news/collect")
    public String collectNews(RedirectAttributes redirectAttributes) throws Exception {
        int savedCount = rssService.collectNews();
        redirectAttributes.addAttribute("savedCount", savedCount);
        return "redirect:/news";
    }
}
