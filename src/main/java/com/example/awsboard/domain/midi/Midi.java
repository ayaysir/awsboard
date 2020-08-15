package com.example.awsboard.domain.midi;

import com.example.awsboard.domain.BaseTimeEntity;
import com.example.awsboard.web.dto.midi.MidiRequestDTO;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Getter
@NoArgsConstructor
@Entity
public class Midi extends BaseTimeEntity {

    @Id // PK 필드
    @GeneratedValue(strategy = GenerationType.IDENTITY) // PK 생성규칙
    private Long id;

    private Long userId;    // 업로드한 사람

    private String category;

    private String customTitle;

    private String hash;

    private String originalMidiPath;

    private String originalMp3Path;

    private String originalFileName;

    @Builder
    public Midi(Long userId, String category, String customTitle,
                String hash, String originalMidiPath, String originalMp3Path,
                String originalFileName) {
        this.userId = userId;
        this.category = category;
        this.customTitle = customTitle;
        this.hash = hash;
        this.originalMidiPath = originalMidiPath;
        this.originalMp3Path = originalMp3Path;
        this.originalFileName = originalFileName;
    }

    public void update(MidiRequestDTO dto) {
        // userid는 변경될 수 없음
        this.category = dto.getCategory() != null ? dto.getCategory() : this.category;
        this.customTitle = dto.getCustomTitle() != null ? dto.getCustomTitle() : this.customTitle;
        this.hash = dto.getHash() != null ? dto.getHash() : this.hash;
        this.originalMidiPath = dto.getOriginalMidiPath() != null ? dto.getOriginalMidiPath() : this.originalMidiPath;
        this.originalMp3Path = dto.getOriginalMp3Path() != null ? dto.getOriginalMp3Path() : this.originalMp3Path;
    }
}
