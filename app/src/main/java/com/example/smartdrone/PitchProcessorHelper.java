package com.example.smartdrone;

import be.tarsos.dsp.AudioDispatcher;
import be.tarsos.dsp.io.android.AudioDispatcherFactory;

public class PitchProcessorHelper {
    private static AudioDispatcher dispatcher =
            AudioDispatcherFactory.fromDefaultMicrophone(22050,1024,0); //TODO make constants for these/ find out what they mean

    public static long timeRegistered;
    public static int noteLengthFilter = 60;


    public static AudioDispatcher getDispatcher() {
        return dispatcher;
    }

    public static long getTimeRegistered() {
        return timeRegistered;
    }

    public static void setTimeRegistered(long curTime) {
        timeRegistered = curTime;
    }

    public static int getNoteLengthFilter() {
        return noteLengthFilter;
    }

    public static void setNoteLengthFilter(int seconds) {
        noteLengthFilter = seconds;
    }

    public static boolean noteMeetsConfidence() {
        return (System.currentTimeMillis() - timeRegistered) > noteLengthFilter;
    }

    public static void incrementNoteLengthFilter() {
        noteLengthFilter = (noteLengthFilter + 15) % 165;
    }
}