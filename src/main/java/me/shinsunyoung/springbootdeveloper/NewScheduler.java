package me.shinsunyoung.springbootdeveloper;


import lombok.RequiredArgsConstructor;
import me.shinsunyoung.springbootdeveloper.service.RssService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class NewScheduler {

    private final RssService rssService;

    @Scheduled(fixedRateString = "${rss.collect-fixed-rate}")  // 10분마다 자동 실행
    public void scheduled() throws Exception {
        rssService.collectNews();
    }
}
