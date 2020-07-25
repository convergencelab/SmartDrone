package com.convergencelabstfx.smartdrone;

import com.convergencelabstfx.keyfinder.MusicTheory;
import com.convergencelabstfx.keyfinder.harmony.VoicingTemplate;
import com.convergencelabstfx.smartdrone.models.ChordConstructor;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {

    @Test
    public void chordConstructor() {
        final ChordConstructor chordConstructor = new ChordConstructor();
        final List<Integer> mode = new ArrayList<>();
        mode.add(0);
        mode.add(2);
        mode.add(3);
        mode.add(5);
        mode.add(7);
        mode.add(9);
        mode.add(10);

        int key = 0;
        final VoicingTemplate template = new VoicingTemplate();
        template.addBassTone(0);
        template.addBassTone(4);

        template.addChordTone(1);
        template.addChordTone(2);
        template.addChordTone(4);
        template.addChordTone(8);

        chordConstructor.setMode(mode);
        chordConstructor.setKey(0);
        chordConstructor.setTemplate(template);
        chordConstructor.setBounds(36, 60, 51, 72);



//        String str = "";
//        for (Integer note : chordConstructor.makeVoicing()) {
//            str += note + ", ";
//        }
        System.out.println(chordConstructor.makeVoicing());
    }
}