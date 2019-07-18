package com.convergencelab.smartdrone.templatecreator;

import android.content.SharedPreferences;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
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

        SharedPreferences mPreferences = android.support.v7.preference.PreferenceManager.getDefaultSharedPreferences(this);
        TemplateCreatorDataSource dataSource = new TemplateCreatorDataSourceImpl(mPreferences);
        mTemplateCreatorPresenter = new TemplateCreatorPresenter(dataSource, templateCreatorFragment);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
