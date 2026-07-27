package me.shinsunyoung.springbootdeveloper.news;


import lombok.RequiredArgsConstructor;
import me.shinsunyoung.springbootdeveloper.rss.RssService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class NewsScheduler {

    private final RssService rssService;

    @Scheduled(fixedRateString = "${rss.collect-fixed-rate}")
    public void scheduled() throws Exception {
        rssService.collectNews();
    }
}
