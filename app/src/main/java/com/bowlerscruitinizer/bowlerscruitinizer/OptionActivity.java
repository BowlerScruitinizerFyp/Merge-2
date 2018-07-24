package com.bowlerscruitinizer.bowlerscruitinizer;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class OptionActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_option);
    }

    public void demoStart(View view) {

        Intent i = new Intent(getBaseContext(), DemoActivity.class);
        startActivity(i);

    }

    public void login(View view) {

        Intent i = new Intent(getBaseContext(), LoginActivity.class);
        startActivity(i);

    }
}
