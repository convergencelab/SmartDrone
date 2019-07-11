package com.convergencelab.smartdrone.Models;

import com.example.keyfinder.HarmonyGenerator;
import com.example.keyfinder.Voicing;

public class HarmonyGeneratorModel {
    private HarmonyGenerator generator;

    public HarmonyGeneratorModel() {
        generator = new HarmonyGenerator();
    }

    public HarmonyGenerator getGenerator() {
        return generator;
    }
}
