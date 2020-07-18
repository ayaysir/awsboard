package com.example.awsboard.domain.notice;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface NoticeRepository extends JpaRepository<Notice, Long> {

    // Entity로 만든 테이블은 대소문자를 구분해야 함 (Posts)
    @Query("select p from Notice p order by p.id desc")
    List<Notice> findAllDesc();

}