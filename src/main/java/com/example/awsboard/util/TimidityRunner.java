package com.example.awsboard.util;

import java.io.*;

public class TimidityRunner {

    // 여기로 보낼 때 루트 패스 포함해서 보냄.
    public File convertMidiToMp3(String midiPath) throws IOException {

//        String rootPath = FileSystemView.getFileSystemView().getHomeDirectory().toString();

        Integer lastIndexOfDot = midiPath.lastIndexOf(".");
        String wavPath = midiPath.substring(0, lastIndexOfDot) + ".wav";
        String mp3Path = midiPath.substring(0, lastIndexOfDot) + ".mp3";

        String[] midiCmd = {"timidity", midiPath, "-o", wavPath, "-Ow"};
        ProcessBuilder midiBuilder = new ProcessBuilder(midiCmd);
        midiBuilder.redirectErrorStream(true);

        Process midiProcess = midiBuilder.start();
        InputStream is = midiProcess.getInputStream();
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));

        String line = null;
        while ((line = reader.readLine()) != null) {
            System.out.println(line);
        }

        System.out.println("==========================================");

        // wav 파일 생성 후
        File wavFile = new File(wavPath);
        if(wavFile.exists() && wavFile.length() > 0) {
            String[] lameCmd = {"lame", wavPath, mp3Path};
            ProcessBuilder lameBuilder = new ProcessBuilder(lameCmd);
            lameBuilder.redirectErrorStream(true);

            Process lameProcess = lameBuilder.start();
            is = lameProcess.getInputStream();
            reader = new BufferedReader(new InputStreamReader(is));

            line = null;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }

            // mp3 파일 있는지 체크
            File mp3File = new File(mp3Path);
            if(mp3File.exists() && mp3File.length() > 0) {
                System.out.println(">> Convert succeeded.");
                return mp3File;
            } else {
                throw new IllegalStateException("Failed to convert mp3 file.");
            }
        } else {
            throw new IllegalStateException("Failed to convert wave file.");
        }

    }

    public static void main(String[] args) throws IOException {

        String midiPath = "/Users/yoonbumtae/Documents/midi/canyon.mid";

        TimidityRunner timidityRunner = new TimidityRunner();
        timidityRunner.convertMidiToMp3(midiPath);

    }
}