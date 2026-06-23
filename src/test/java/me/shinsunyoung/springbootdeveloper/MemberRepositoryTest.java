package me.shinsunyoung.springbootdeveloper;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.jdbc.Sql;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class MemberRepositoryTest {

    @Autowired
    MemberRepository memberRepository;

    @Sql("/insert-members.sql")     // 테스트 실행 전 SQL 스크립트 실행
    @Test
    void getAllMembers(){
        //when
        List<Member> members = memberRepository.findAll();

        //then
        assertThat(members.size()).isEqualTo(3);
    }

    @Sql("/insert-members.sql")
    @Test
    void getMemberById(){
        //when
        Member member = memberRepository.findById(2L).get();

        //then
        assertThat(member.getName()).isEqualTo("B");
    }

    @Sql("/insert-members.sql")
    @Test
    void getMemberByName(){
        //when
        Member member = memberRepository.findByName("C").get();

        //then
        assertThat(member.getId()).isEqualTo(3);
    }


    @Test
    void saveMember(){
        //given
        Member member = new Member(1L, "A");

        //when
        memberRepository.save(member);

        //then
        assertThat(memberRepository.findById(1L).get().getName()).isEqualTo("A");
    }

    @Test
    void saveMembers(){
        //given
        List<Member> members = List.of(new Member(2L, "B"), new Member(3L, "C"));

        //when
        memberRepository.saveAll(members);

        //then
        assertThat(memberRepository.findAll().size()).isEqualTo(2);

    }

    @Sql("/insert-members.sql")
    @Test
    void deleteMemberById(){
        // when
        memberRepository.deleteById(2L);
        //then
        assertThat(memberRepository.findById(2L).isEmpty()).isTrue();
    }

    @AfterEach
    void deleteAll(){
        // 테스트 간 영향을 주지 않도록 하기 위해 사용
        memberRepository.deleteAll();
    }

    // Member.java에 changeName 함수 추가
    @Sql("/insert-members.sql")
    @Test
    void update(){
        // given
        Member member = memberRepository.findById(2L).get();
        //when
        member.changeName("BA");
        //then
        assertThat(memberRepository.findById(2L).get().getName()).isEqualTo("BA");
    }
    // @Transactional : 영속 상태일 때 필드값 변경 후 트랜잭션 커밋이 되면 JPA는 변경 사항을 DB에 자동 적용한다.
    // @DataJpaTest에 @Transactional 애너테이션이 있어서 DB에 대한 트랜잭션 관리를 설정한다.

}