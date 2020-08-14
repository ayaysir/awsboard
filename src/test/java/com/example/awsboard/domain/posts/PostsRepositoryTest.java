package com.example.awsboard.domain.posts;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class PostsRepositoryTest {

    @Autowired
    PostsRepository postsRepository;

    @AfterEach
    public void cleanup() { // 데이터 섞임 방지
        postsRepository.deleteAll();
    }

    @Test
    public void 게시글저장_불러오기() {
        // given
        String title = "Test title";
        String content = "Test content";

        postsRepository.save(Posts.builder()
                .title(title)
                .content(content)
                .author("kim")
                .build());

        // when
        List<Posts> postsList = postsRepository.findAll();

        // then
        Posts posts = postsList.get(0);
        assertThat(posts.getTitle()).isEqualTo(title);
        assertThat(posts.getContent()).isEqualTo(content);
    }

    @Test
    public void BaseTimeEntry_등록() {
        // given
        LocalDateTime now = LocalDateTime.of(2019, 6, 4, 0, 0, 0);
        postsRepository.save(Posts.builder()
                .title("title")
                .content("contents")
                .author("author")
                .build());

        // when
        List<Posts> postsList = postsRepository.findAll();

        // then
        Posts posts = (Posts) postsList.get(0);

        System.out.println(">>>>>> createDate=" + posts.getCreatedDate() + ", modifiedDate" + posts.getModifiedDate());

        assertThat(posts.getCreatedDate()).isAfter(now);
        assertThat(posts.getModifiedDate()).isAfter(now);


    }

    @Test
    public void Search_동작여부() {

        // Given
        String title = "Dog water";
        String content = "Cat water";

        postsRepository.save(Posts.builder()
                .title(title)
                .content(content)
                .author("kim")
                .build());
        postsRepository.save(Posts.builder()
                .title(content)
                .content(title)
                .author("park")
                .build());
        postsRepository.save(Posts.builder()
                .title("fire")
                .content("fire")
                .author("lee")
                .build());

        // when
        String toFindKeyword ="Cat";

        List<Posts> postsList = postsRepository
                .findByContentContainingIgnoreCaseOrTitleContainingIgnoreCase(toFindKeyword, toFindKeyword);

        // then
        Posts posts0 = (Posts) postsList.get(0);
        Posts posts1 = (Posts) postsList.get(1);

        System.out.println(postsList);

        assertThat(posts0.getContent()).contains(toFindKeyword);
        assertThat(posts1.getTitle()).contains(toFindKeyword);

    }

}
