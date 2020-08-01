package com.example.awsboard.domain.midi;

import org.springframework.data.jpa.repository.JpaRepository;

public interface MidiRepository extends JpaRepository<Midi, Long> {

    Midi findByHash(String hash);
}
