package com.example.awsboard.domain.log;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface LogRepository extends JpaRepository<Log, Long> {

    // native query인 경우 테이블 이름도 소문자로 써야함
    @Query(value = "select * from log a where a.board_name = :boardName and a.article_id = :articleId", nativeQuery = true)
    List<Log> getListGroupByBoardNameAndArticleId(@Param("boardName") String boardName, @Param("articleId") Long articleId);
}
