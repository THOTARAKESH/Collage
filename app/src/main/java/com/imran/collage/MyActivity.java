package com.imran.collage;

import android.app.Activity;
import android.os.Bundle;
import com.crashlytics.android.Crashlytics;


public class MyActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Crashlytics.start(this);
        setContentView(R.layout.activity_my);
    }
}
