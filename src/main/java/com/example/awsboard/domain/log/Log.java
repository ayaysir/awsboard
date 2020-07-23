package com.example.awsboard.domain.log;

import com.example.awsboard.domain.BaseTimeEntity;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Getter
@NoArgsConstructor
@Entity // 테이블과 링크될 클래스
public class Log extends BaseTimeEntity {

    @Id // PK 필드
    @GeneratedValue(strategy = GenerationType.IDENTITY) // PK 생성규칙
    private Long id;

    private String boardName;

    private Long articleId;

    private Long userId;

    @Builder
    public Log(String boardName, Long articleId, Long userId) {
        this.boardName = boardName;
        this.articleId = articleId;
        this.userId = userId;
    }
}
