package me.shinsunyoung.springbootdeveloper.service;

import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class RssClient {

    private static final String RSS_URL =
            "https://news.google.com/rss?hl=ko&gl=KR&ceid=KR:ko";

    private final RestClient restClient = RestClient.create();

    public String fetch(){
        return restClient.get()
                .uri(RSS_URL)
                .retrieve()
                .body(String.class);
    }
}
