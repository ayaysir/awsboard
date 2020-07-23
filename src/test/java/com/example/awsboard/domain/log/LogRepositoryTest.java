package com.example.awsboard.domain.log;

import com.example.awsboard.domain.posts.Posts;
import com.example.awsboard.domain.posts.PostsRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class LogRepositoryTest {

    @Autowired
    LogRepository logRepository;

    @AfterEach
    public void cleanup() { // 데이터 섞임 방지
        logRepository.deleteAll();
    }

    @Test
    public void log_save_and_load() {
        // given
        Long articleId = 1l;
        Long userId = 1l;
        String boardName = "Posts";

        logRepository.save(Log.builder().articleId(articleId).userId(userId).boardName(boardName).build());

        // when
        List<Log> logList = logRepository.findAll();

        // then
        Log log = logList.get(0);
        assertThat(log.getArticleId()).isEqualTo(articleId);
        assertThat(log.getBoardName()).isEqualTo(boardName);
    }

    @Test
    public void BaseTimeEntry_등록() {
        // given
        LocalDateTime now = LocalDateTime.of(2019, 6, 4, 0, 0, 0);

        Long articleId = 1l;
        Long userId = 1l;
        String boardName = "Posts";
        logRepository.save(Log.builder().articleId(articleId).userId(userId).boardName(boardName).build());

        // when
        List<Log> logList = logRepository.findAll();

        // then
        Log log = logList.get(0);

        System.out.println(">>>>>> createDate=" + log.getCreatedDate() + ", modifiedDate" + log.getModifiedDate());

        assertThat(log.getCreatedDate()).isAfter(now);
        assertThat(log.getModifiedDate()).isAfter(now);


    }
}
