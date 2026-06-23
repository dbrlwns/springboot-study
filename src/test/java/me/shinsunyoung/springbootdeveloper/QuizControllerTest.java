package me.shinsunyoung.springbootdeveloper;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.context.WebApplicationContext;

import javax.xml.transform.Result;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@SpringBootTest
@AutoConfigureMockMvc
class QuizControllerTest {

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private ObjectMapper objectMapper; // Jackson : 클래스로 객체와 JSON간 변환 처리

    @BeforeEach
    public void mockMvcSetUp(){
        mockMvc = MockMvcBuilders.webAppContextSetup(context).build();
    }

    @DisplayName("quiz(): GET /quiz?code=1 이면 응답 코드는 201," +
            "응답 본문은 Created! 를 리턴한다")
    @Test
    public void getQuiz1() throws Exception {
        //given
        final String url = "/quiz";

        //when
        final ResultActions result = mockMvc.perform(get(url)
                        .param("code", "1")
                .contentType(MediaType.APPLICATION_JSON));

        //then
        result.andExpect(status().isCreated())
//                .andExpect(jsonPath("$[0].code").value("Created!"));
                .andExpect(content().string("created!"));

    }

    @DisplayName("quiz(): GET /quiz?code=2 이면 응답 코드는 400, 응답 본문은" +
            "Bad Request!를 리턴한다.")
    @Test
    public void getQuiz2() throws Exception {
        //given
        final String url = "/quiz";

        //when
        final ResultActions result = mockMvc.perform(get(url)
                        .param("code", "2")
                .contentType(MediaType.APPLICATION_JSON));


        //then
        result.andExpect(status().isBadRequest())
//                .andExpect(jsonPath("$[0].code").value("Bad Request!"));
                .andExpect(content().string("Bad Request!"));

    }


    // 아래부터는 post 요청이므로 .param으로 데이터 전송할 수 없음
    // 따라서 json 파싱하여 사용

    @DisplayName("quiz(): POST /quiz?code=1 이면 응답 코드는 403," +
            "응답 본문은 Forbidden!을 리턴한다.")
    @Test
    public void postQuiz1() throws Exception {
        final String url = "/quiz";
        final ResultActions result = mockMvc.perform(post(url)
                        .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(new Code(1))));

        result.andExpect(status().isForbidden())
                .andExpect(content().string("Forbidden!"));
    }


    @DisplayName("quiz(): POST /quiz?code=13 이면 응답 코드는 200, 응답 본문은 OK! 를 리턴")
    @Test
    public void postQuiz2() throws Exception {
        final String url = "/quiz";
        final ResultActions result = mockMvc.perform(post(url)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new Code(13))));

        result.andExpect(status().isOk())
                .andExpect(content().string("OK!"));
    }

}