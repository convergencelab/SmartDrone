package com.convergencelabstfx.keyfinder;

import com.convergencelabstfx.keyfinder.harmony.VoicingTemplate;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() {
        assertEquals(4, 2 + 2);
    }

    @Test
    public void test_voicingTemplate() {
        VoicingTemplate template = new VoicingTemplate();
        template.addBassTone(0);
        template.addBassTone(4);

        template.addChordTone(1);
        template.addChordTone(2);

        template.addChordTone(5);

        template.addChordTone(3);

        template.addChordTone(5);

        template.removeChordTone(5);

        template.removeChordTone(1);

        System.out.println(template.toString());
    }

}