package com.convergencelabstfx.smartdrone;


import android.content.Context;
import android.content.SharedPreferences;

import com.convergencelabstfx.smartdrone.utility.DronePreferences;

import com.example.keyfinder.VoicingTemplate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;

public class VoicingHelper {

    //todo do not allow users to put commas in their voicing name, it could mess everything up
    //todo make voicing require name, with only certain characters allowed
    /**
     * Makes a flattened string copy of a voicing template.
     * @param       template VoicingTemplate; voicing template.
     * @return      String; flattened string of voicing template.
     */
    //todo needs refactoring
    public static String encodeTemplate(VoicingTemplate template) {
        StringBuilder templateStr = new StringBuilder();
        templateStr.append(template.getName());

        // Add Bass Tones.
        templateStr.append('{');
        for (int i = 0; i < template.getBassTones().length; i++) {
            // So string only has comma in between values,
            // not before first or after last.
            if (i > 0) {
                templateStr.append(',');
            }
            templateStr.append(template.getBassTones()[i].getDegree());
        }
        templateStr.append('}');

        // Add Chord Tones.
        templateStr.append('{');
        for (int i = 0; i < template.getChordTones().length; i++) {
            // So string only has comma in between values,
            // not before first or after last.
            if (i > 0) {
                templateStr.append(',');
            }
            templateStr.append(template.getChordTones()[i].getDegree());
        }
        templateStr.append('}');

        return templateStr.toString();
    }

    /**
     * Constructs a voicing template from a flattened template string.
     * @param       flattenedTemplate String; flattened template.
     * @return      VoicingTemplate; voicing template.
     */
    //todo needs refactoring
    public static VoicingTemplate decodeTemplate(String flattenedTemplate) {
        // 0) Name, 1) BassTones, 2) ChordTones
        StringBuilder[] templateStrs = new StringBuilder[]{new StringBuilder(), new StringBuilder(), new StringBuilder()}; // Name, Bass Tones, Chord Tones
        int templateStrIx = 0;

        for (int i = 0; i < flattenedTemplate.length(); i++) {
            char curChar = flattenedTemplate.charAt(i);
            if (templateStrIx == 0) {
                // Start of bass tones found.
                if (curChar == '{') {
                    templateStrIx++;
                }
                else {
                    templateStrs[templateStrIx].append(curChar);
                }
            }
            else {
                // Start of chord tones found.
                if (curChar == '{') {
                    templateStrIx++;
                }
                else if (curChar != '}') {
                    templateStrs[templateStrIx].append(curChar);
                }
            }
        }

        String[] bassStr = templateStrs[1].toString().split(",");
        int[] bassIxs;
        if (!templateStrs[1].toString().isEmpty()) {
            bassIxs = new int[bassStr.length];
            for (int i = 0; i < bassIxs.length; i++) {
                bassIxs[i] = Integer.parseInt(bassStr[i]);
            }
        }
        else {
            bassIxs = new int[]{};
        }

        String[] chordStr = templateStrs[2].toString().split(",");
        int[] chordIxs;
        if (!templateStrs[2].toString().isEmpty()) {
            chordIxs = new int[chordStr.length];
            for (int i = 0; i < chordIxs.length; i++) {
                chordIxs[i] = Integer.parseInt(chordStr[i]);
            }
        }
        else {
            chordIxs = new int[]{};
        }

        return new VoicingTemplate(templateStrs[0].toString(), bassIxs, chordIxs);
    }

    /**
     * Turns string of all flattened voicings into array list of flattened voicings.
     * @param       flattenedList String; string with all flattened templates.
     * @return      ArrayList; list of all flattened templates.
     */
    public static ArrayList inflateTemplateList(String flattenedList) {
        String[] strArr = flattenedList.split("\\|");
        return new ArrayList(Arrays.asList(strArr));
    }

    /**
     * Constructs string of all contents of array list containing flattened voicing templates.
     * @param       templateList ArrayList; flattened voicing templates.
     * @return      String; flattened voicing templates.
     */
    public static String flattenTemplateList(ArrayList<String> templateList) {
        if (templateList.isEmpty()) { //todo replace with exception
            return Constants.DEFAULT_TEMPLATE_LIST;
        }

        // Grab the first template so pipes are only inserted in between voicings
        StringBuilder flattenedTemplates = new StringBuilder(templateList.get(0));
        for (int i = 1; i < templateList.size(); i++) {
            flattenedTemplates.append('|');
            flattenedTemplates.append(templateList.get(i));
        }
        return flattenedTemplates.toString();
    }

    /**
     * Get the name of a flattened template.
     * @param       flattenedTemplate String; flattened template.
     * @return      String; name of template.
     */
    public static String getTemplateName(String flattenedTemplate) {
        StringBuilder name = new StringBuilder();
        name.append(flattenedTemplate.charAt(0));
        // todo why the f is this 1?
        int i = 1;
        while (flattenedTemplate.charAt(i) != '{') {
            name.append(flattenedTemplate.charAt(i));
            i++;
        }
        return name.toString();
    }

    public static void addTemplateToPref(SharedPreferences preferences, VoicingTemplate template) {
        String flattenedTemplate = encodeTemplate(template);
        String allTemplates = DronePreferences.getAllTemplatePref(preferences);
        allTemplates += '|' + flattenedTemplate;
        DronePreferences.setAllTemplatePref(preferences, allTemplates);
    }

    public static HashSet<String> getSetOfAllTemplateNames(String flattenedTemplateList) {
        HashSet<String> allNames = new HashSet<>();
        boolean nameFound = false;
        StringBuilder curName = new StringBuilder();
        char curChar;
        for (int i = 0; i < flattenedTemplateList.length(); i++) {
            curChar = flattenedTemplateList.charAt(i);
            if (!nameFound && curChar != '{') {
                curName.append(curChar);
            }
            else if (curChar == '{') {
                nameFound = true;
                allNames.add(curName.toString());
                // Clear the previous name.
                curName.setLength(0);
            }
            else if (nameFound && curChar == '|') {
                nameFound = false;
            }
        }
        return allNames;
    }

    @Deprecated
    public static void addTemplateToPref(Context context, String toAdd) {
        String allTemplates = DronePreferences.getAllTemplatePref(context);
        allTemplates += '|' + toAdd;
        DronePreferences.setAllTemplatePref(context, allTemplates);
    }
}
