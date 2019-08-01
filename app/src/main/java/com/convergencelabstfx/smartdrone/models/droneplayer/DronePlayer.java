package com.convergencelabstfx.smartdrone.models.droneplayer;

public interface DronePlayer {

    void start();

    void stop();

    void play(int[] toPlay);

    void play(int toPlay);

    void add(int[] toAdd);

    void add(int toAdd);

    void remove(int[] toRemove);

    void remove(int toRemove);

    boolean noteIsActive(int target);

    int getPlugin();

    void setPlugin(int plugin);

    int getVolume();

    void setVolume(int volume);

    void mute();

    void unmute();

}
