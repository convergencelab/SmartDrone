package com.convergencelabstfx.smartdrone;

import android.content.Context;

import com.convergencelabstfx.smartdrone.Utility.DronePreferences;
import com.example.smartdrone.Voicing;
import com.example.smartdrone.VoicingTemplate;

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
    public static String flattenTemplate(VoicingTemplate template) {
        String templateStr = "";
        templateStr += template.getName();
        for (int i = 0; i < template.size(); i++) {
            templateStr += ',' + Integer.toString(template.getChordTone(i));
        }
        return templateStr;
    }

    /**
     * Constructs a voicing template from a flattened template string.
     * @param       flattenedTemplate String; flattened template.
     * @return      VoicingTemplate; voicing template.
     */
    public static VoicingTemplate inflateTemplate(String flattenedTemplate) {
        String[] str = flattenedTemplate.split(",");
        String name = str[0];
        int[] chordTones = new int[str.length - 1];
        for (int i = 1; i < str.length; i++) {
            chordTones[i - 1] = Integer.parseInt(str[i]);
        }
        return new VoicingTemplate(name, chordTones);
    }

    /**
     * Makes a flattened string copy of a voicing.
     * @param       voicing Voicing; voicing.
     * @return      String; flattened string of voicing.
     */
    public static String flattenVoicing(Voicing voicing) {
        String voicingStr = "";
        //todo refactor voicing class to be consistent with template class
        for (int i = 0; i < voicing.getVoiceIxs().length; i++) {
            voicingStr += voicing.getVoiceIxs()[i];
            if (i != voicing.getVoiceIxs().length) {
                voicingStr += ',';
            }
        }
        return voicingStr;
    }

    /**
     * Turns string of all flattened voicings into array list of flattened voicings.
     * @param       flattenedList String; string with all flattened templates.
     * @return      ArrayList; list of all flattened templates.
     */
    public static ArrayList<String> inflateTemplateList(String flattenedList) {
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
            return null;
        }
        String flattenedTemplates = templateList.get(0); //todo error check; but should work for meantime IF must have one voicing rule imposed
        for (int i = 1; i < templateList.size(); i++) {
            flattenedTemplates += "|" + templateList.get(i);
        }
        return flattenedTemplates;
    }

    /**
     * Get the name of a flattened template.
     * @param       flattenedTemplate String; flattened template.
     * @return      String; name of template.
     */
    public static String getTemplateName(String flattenedTemplate) {
        String name = "";
        int i = 1;
        name += flattenedTemplate.charAt(0);
        while (flattenedTemplate.charAt(i) != ',') {
            name += flattenedTemplate.charAt(i);
            i++;
        }
        return name;
    }

    public static void addTemplateToPref(Context context, String toAdd) {
        String allTemplates = DronePreferences.getAllTemplatePref(context);
        allTemplates += '|' + toAdd;
        DronePreferences.setAllTemplatePref(context, allTemplates);
    }



    public static HashSet<String> getSetOfAllTemplateNames(String flattenedTemplateList) {
        HashSet<String> allNames = new HashSet<>();
        boolean nameFound = false;
        String curString = "";
        char curChar;
        for (int i = 0; i < flattenedTemplateList.length(); i++) {
            curChar = flattenedTemplateList.charAt(i);
            if (!nameFound && curChar != ',') {
                curString += curChar;
            }
            else if (!nameFound && curChar == ',') {
                nameFound = true;
                allNames.add(curString);
                curString = "";
            }
            else if (nameFound && curChar == '|') {
                nameFound = false;
            }
        }
        return allNames;
    }
}
