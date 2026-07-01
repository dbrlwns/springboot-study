package me.shinsunyoung.springbootdeveloper.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class RssClient {

    @Value("${rss.google-rss-url}")
    private String rssUrl;

    private final RestClient restClient = RestClient.create();

    public String fetch(){
        return restClient.get()
                .uri(rssUrl)
                .retrieve()
                .body(String.class);
    }
}
