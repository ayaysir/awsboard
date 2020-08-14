package com.example.awsboard.util;

import java.io.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class TimidityRunner {

    // 여기로 보낼 때 루트 패스 포함해서 보냄.
    public static Boolean convertMidiToMp3(String midiPath, String mp3Path) throws IOException {

        Integer lastIndexOfDot = mp3Path.lastIndexOf(".");
        String wavPath = mp3Path.substring(0, lastIndexOfDot) + ".wav";
//        String mp3Path = midiPath.substring(0, lastIndexOfDot) + ".mp3";

        String[] midiCmd = {"/usr/local/bin/timidity", midiPath, "-o", wavPath, "-Ow"};
        System.out.println("** run >>>> " + midiCmd[0] + " " + midiCmd[1]
                + " " + midiCmd[2] + " " + midiCmd[3] + " " + midiCmd[4]);
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
            String[] lameCmd = {"/usr/local/bin/lame", wavPath, mp3Path};
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
                wavFile.delete();
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }

    }

    public static String getHash(String path) throws IOException, NoSuchAlgorithmException {

        MessageDigest messageDigest = MessageDigest.getInstance("MD5");
        FileInputStream fileInputStream = new FileInputStream(path);

        byte[] dataBytes = new byte[1024];

        Integer nRead = 0;
        while((nRead = fileInputStream.read(dataBytes)) != -1) {
            messageDigest.update(dataBytes, 0, nRead);
        }

        byte[] mdBytes = messageDigest.digest();

        StringBuffer stringBuffer = new StringBuffer();
        for(Integer i = 0; i < mdBytes.length; i++) {
            stringBuffer.append(Integer.toString((mdBytes[i] & 0xff) + 0x100, 16)).substring(1);
        }

        return stringBuffer.toString();

    }

    public static void main(String[] args) throws IOException, NoSuchAlgorithmException {

        String midiPath = "/Users/yoonbumtae/Documents/midi/canyon.mid";
        String mp3Path = "/Users/yoonbumtae/Documents/midi/mp3/canyon.mp3";

        File mp3Dir = new File("/Users/yoonbumtae/Documents/midi/mp3/");
        if(!mp3Dir.exists())    mp3Dir.mkdirs();

        System.out.println(TimidityRunner.getHash(midiPath));
        TimidityRunner.convertMidiToMp3(midiPath, mp3Path);

    }
}