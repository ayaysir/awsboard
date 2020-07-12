package com.example.awsboard.domain.posts;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface PostsRepository extends JpaRepository<Posts, Long> {

    // Entity로 만든 테이블은 대소문자를 구분해야 함 (Posts)
    @Query("select p from Posts p order by p.id desc")
    List<Posts> findAllDesc();

}
