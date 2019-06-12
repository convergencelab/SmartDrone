package com.example.smartdrone;

public class VoicingHelper {

    //todo do not allow users to put commas in their voicing name, it could mess everything up
    //todo make voicing require name, with only certain characters allowed
    /**
     * Makes a flattened string copy of a voicing template.
     * @param       template VoicingTemplate; voicing template.
     * @return      String; flattened string of voicing template.
     */
    public static String flattenVoicingTemplate(VoicingTemplate template) {
        String templateStr = "";
        templateStr += template.getName();
        for (int i = 0; i < template.size(); i++) {
            templateStr += ',' + Integer.toString(template.getChordTone(i));
        }
        return templateStr;
    }

    public static VoicingTemplate restoreVoicingTemplate(String flattenedTemplate) {
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
}
