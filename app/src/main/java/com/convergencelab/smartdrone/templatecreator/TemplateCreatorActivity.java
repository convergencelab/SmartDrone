package com.convergencelab.smartdrone.templatecreator;

import android.content.SharedPreferences;
import androidx.fragment.app.FragmentTransaction;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

import com.convergencelab.smartdrone.R;

public class TemplateCreatorActivity extends AppCompatActivity {

    private TemplateCreatorPresenter mTemplateCreatorPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_template_creator);

        TemplateCreatorFragment templateCreatorFragment = (TemplateCreatorFragment) getSupportFragmentManager()
                .findFragmentById(R.id.contentFrame);

        if (templateCreatorFragment == null) {
            templateCreatorFragment = TemplateCreatorFragment.newInstance();
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.add(R.id.contentFrame, templateCreatorFragment);
            transaction.commit();
        }

        SharedPreferences mPreferences = androidx.preference.PreferenceManager.getDefaultSharedPreferences(this);
        TemplateCreatorDataSource dataSource = new TemplateCreatorDataSourceImpl(mPreferences);
        mTemplateCreatorPresenter = new TemplateCreatorPresenter(dataSource, templateCreatorFragment);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
