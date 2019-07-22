package com.convergencelab.smartdrone;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.widget.TextView;

public class AboutActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        loadVersionName();
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

        TextView demoVideoLink = findViewById(R.id.manual_demovideo);
        demoVideoLink.setMovementMethod(LinkMovementMethod.getInstance());
    }

    private void loadVersionName() {
        try {
            PackageInfo pInfo = getApplicationContext().getPackageManager().getPackageInfo(getPackageName(), 0);
            String versionName = pInfo.versionName;
            TextView versionView = findViewById(R.id.version_name);
            versionView.setText("Version: " + versionName);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }
}
