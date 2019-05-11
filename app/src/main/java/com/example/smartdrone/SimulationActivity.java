package com.example.smartdrone;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class SimulationActivity extends AppCompatActivity {

    public static final String MESSAGE_LOG =
            "Simulation";

    public static KeyFinder keyFinder = new KeyFinder();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_simulation);
        // keyFinder.setNoteTimerLength(100);                 // Future User Parameter
        Log.d(MESSAGE_LOG, "Simulation Started.");
        constructNoteButtons();
    }

    public void addNote(View view) {
        String noteIxStr = view.getTag().toString();
        int noteIx = Integer.parseInt(noteIxStr);
        keyFinder.addNoteToList(keyFinder.getAllNotes().getNoteAtIndex(noteIx));
        printActiveKeyToScreen();
        Log.d(MESSAGE_LOG, keyFinder.getActiveNotes().toString()); // active note list
    }

    public void printActiveKeyToScreen() {
        TextView tv = findViewById(R.id.activeKeyPlainText);
        tv.setText("Active Key: " + keyFinder.getActiveKey().getName());
    }

    /**
     * Generate buttons for all 12 notes.
     * Each button will add the corresponding note to the KeyFinder.
     */
    public void constructNoteButtons() {
        // Had to add LinearLayout class in front of LayoutParams
        LinearLayout llLeft = (LinearLayout)findViewById(R.id.noteButtonLayoutLeft);
        LinearLayout llRight = (LinearLayout)findViewById(R.id.noteButtonLayoutRight);
        LinearLayout[] llArr = { llLeft, llRight};
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        // Construct and add note button.
        for (int i = 0; i < 12; i++) {
            final Button curNoteButton = new Button(this);
            curNoteButton.setText(MainActivity.notes[i]);
            // curNoteButton.setTextAlignment(View.TEXT_ALIGNMENT_VIEW_START);
            curNoteButton.setId(i);
            curNoteButton.setTag(i);
            // Set buttons on click to add note
            curNoteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    addNote(curNoteButton);
                }
            });
            llArr[i%2].addView(curNoteButton, lp);
        }
    }
}
