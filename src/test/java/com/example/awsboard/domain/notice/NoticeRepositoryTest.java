package com.example.awsboard.domain.notice;

import com.example.awsboard.domain.posts.Posts;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class NoticeRepositoryTest {

    @Autowired
    NoticeRepository noticeRepository;

    @AfterEach
    public void cleanup() { // 데이터 섞임 방지
        noticeRepository.deleteAll();
    }

    @Test
    public void 게시글저장_불러오기() {
        // given
        String title = "Test title";
        String content = "Test content";

        noticeRepository.save(Notice.builder()
                .title(title)
                .content(content)
                .author("kim")
                .build());

        // when
        List<Notice> noticeList = noticeRepository.findAll();

        // then
        Notice notice = noticeList.get(0);
        assertThat(notice.getTitle()).isEqualTo(title);
        assertThat(notice.getContent()).isEqualTo(content);
    }

    @Test
    public void BaseTimeEntry_등록() {
        // given
        LocalDateTime now = LocalDateTime.of(2019, 6, 4, 0, 0, 0);
        noticeRepository.save(Notice.builder()
                .title("title")
                .content("contents")
                .author("author")
                .build());

        // when
        List<Notice> noticeList = noticeRepository.findAll();

        // then
        Notice notice = (Notice) noticeList.get(0);

        System.out.println(">>>>>> createDate=" + notice.getCreatedDate() + ", modifiedDate" + notice.getModifiedDate());

        assertThat(notice.getCreatedDate()).isAfter(now);
        assertThat(notice.getModifiedDate()).isAfter(now);

    }
}