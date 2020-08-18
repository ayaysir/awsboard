package com.example.awsboard.service.posts;

import com.example.awsboard.domain.midi.Midi;
import com.example.awsboard.domain.midi.MidiRepository;
import com.example.awsboard.web.dto.midi.MidiPublicResponseDTO;
import com.example.awsboard.web.dto.midi.MidiRequestDTO;
import com.example.awsboard.web.dto.midi.MidiResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class MidiService {
    private final MidiRepository midiRepository;

    @Transactional
    public Long save(MidiRequestDTO requestDTO) {
        return midiRepository.save(requestDTO.toEnity()).getId();
    }

    @Transactional
    public Long update(Long id, MidiRequestDTO requestDTO) {
        Midi midi = midiRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 파일이 없습니다. id=" + id));
        midi.update(requestDTO);
        return midi.getId();
    }

    @Transactional
    public void delete(Long id) {
        Midi midi = midiRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 파일이 없습니다. id=" + id));
        midiRepository.delete(midi);
    }

    public MidiResponseDTO findById(Long id) {
        Midi midi = midiRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 파일이 없습니다. id=" + id));

        return new MidiResponseDTO(midi);
    }

    @Transactional(readOnly = true)
    public List<MidiPublicResponseDTO> findAll() {
        return midiRepository.findAll().stream()
                .map(MidiPublicResponseDTO::new)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<MidiPublicResponseDTO> findByUserId(Long userId) {
        return midiRepository.findByUserId(userId).stream()
                .map(MidiPublicResponseDTO::new)
                .collect(Collectors.toList());
    }


}
