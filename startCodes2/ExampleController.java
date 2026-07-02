package me.shinsunyoung.springbootdeveloper.controller;

import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.time.LocalDate;
import java.util.List;

@Controller
public class ExampleController {

    @GetMapping("/thymeleaf/example")
    public String thymeleafExample(Model model) {   // 뷰로 데이터를 넘겨주는 Model
        Person exPerson = new Person();
        exPerson.setId(1L);
        exPerson.setName("hone");
        exPerson.setAge(18);
        exPerson.setHobbies(List.of("exercise", "reading"));

        model.addAttribute("person", exPerson);
        model.addAttribute("today", LocalDate.now());

        return "example";   // example.html 조회
    }

    @Setter
    @Getter
    public class Person {
        private Long id;
        private String name;
        private int age;
        private List<String> hobbies;
    }
}
