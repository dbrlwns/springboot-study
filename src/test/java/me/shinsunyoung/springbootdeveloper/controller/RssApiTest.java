package me.shinsunyoung.springbootdeveloper.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import me.shinsunyoung.springbootdeveloper.config.RssProperties;
import me.shinsunyoung.springbootdeveloper.domain.News;
import me.shinsunyoung.springbootdeveloper.service.RssService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.client.RestClient;
import org.springframework.web.context.WebApplicationContext;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

@SpringBootTest
@AutoConfigureMockMvc
public class RssApiTest {

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    WebApplicationContext context;

    @Autowired
    RssProperties rssProperties;

    @Autowired
    RssService rssService;

    @BeforeEach
    public void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(context).build();
    }

    @DisplayName("Rss test")
    @Test
    @ConfigurationProperties
    void getRssTest() throws Exception {
        //given
        final String url = "https://news.google.com/rss?hl=ko&gl=KR&ceid=KR:ko";

        //when
        String body = RestClient.create()
                .get()
                .uri(url)
                .retrieve()
                .body(String.class);

        //then
        System.out.println(body.substring(0, Math.min(body.length(), 3000)));
        assertThat(body).contains("<rss");

    }

    @DisplayName("Rss test 2") // yaml 동작 확인
    @Test
    void getRssTest2() throws Exception {
        //given
        List<String> newsList = new ArrayList<>();
        List<RssProperties.Feed> feeds = rssProperties.getFeeds();

        //when
        for (RssProperties.Feed feed : feeds) {
            String body = RestClient.create()
                    .get()
                    .uri(feed.getUrl())
                    .retrieve()
                    .body(String.class);
            newsList.add(body.substring(0, Math.min(body.length(), 3000)));
        }

        //then
//        System.out.println(newsList);
        assertThat(feeds).hasSize(2);
        assertThat(feeds).allSatisfy(feed -> assertThat(feed.getUrl()).isNotBlank());
        assertThat(feeds).extracting(RssProperties.Feed::getSource).containsExactly("GOOGLE", "HADA");

        assertThat(newsList).hasSize(2);
        assertThat(newsList).allSatisfy(news -> assertThat(news).isNotEmpty());
    }

}
