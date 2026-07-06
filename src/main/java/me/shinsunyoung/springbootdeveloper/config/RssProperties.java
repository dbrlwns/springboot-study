package me.shinsunyoung.springbootdeveloper.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Component
@ConfigurationProperties(prefix = "rss")
@Getter
@Setter
public class RssProperties {
    private List<Feed> feeds = new ArrayList<>();

    @Getter
    @Setter
    public static class Feed {
        private String url;
        private String source;
    }
}
