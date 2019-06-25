package com.example.smartdrone;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.widget.TextView;

public class AboutActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        loadHyperlinks();
    }

    /**
     * Loads hyperlinks for libraries under the Libraries header.
     */
    private void loadHyperlinks() {
        TextView tarsosLink = findViewById(R.id.libraries_tarsosdsp);
        tarsosLink.setMovementMethod(LinkMovementMethod.getInstance());

        TextView midiDriverLink = findViewById(R.id.libraries_mididriver);
        midiDriverLink.setMovementMethod(LinkMovementMethod.getInstance());
    }
}
