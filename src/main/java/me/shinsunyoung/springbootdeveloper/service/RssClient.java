package me.shinsunyoung.springbootdeveloper.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class RssClient {

    private final RestClient restClient = RestClient.create();

    public String fetch(String rssUrl){
        return restClient.get()
                .uri(rssUrl)
                .retrieve()
                .body(String.class);
    }
}
