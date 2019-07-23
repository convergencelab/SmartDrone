package com.convergencelab.smartdrone.models.data;

public class Plugin {

    private final String mName;
    private final int mPlugin;

    Plugin(String name, int plugin) {
        mName = name;
        mPlugin = plugin;
    }

    public String getName() {
        return mName;
    }

    public int getPlugin() {
        return mPlugin;
    }
}
