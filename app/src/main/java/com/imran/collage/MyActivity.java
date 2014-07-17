package com.imran.collage;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;

import com.crashlytics.android.Crashlytics;
import com.imran.collage.views.CollageView;


public class MyActivity extends Activity {


    CollageView mCollageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Crashlytics.start(this);
        setContentView(R.layout.activity_my);
        mCollageView = (CollageView) findViewById(R.id.collage_view);

        if (savedInstanceState == null) {

        } else {
            CollageView.ImageContainer[] imageContainers = mCollageView.getImageContainers();
            for (CollageView.ImageContainer im : imageContainers) {
                im.updateImage((Uri) savedInstanceState.getParcelable(String.valueOf(im.position)));
            }
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        CollageView.ImageContainer[] imageContainers = mCollageView.getImageContainers();
        for (CollageView.ImageContainer im : imageContainers) {
            if(im.imageUri != null) {
                outState.putParcelable(String.valueOf(im.position), im.imageUri);
            }
        }
        super.onSaveInstanceState(outState);
    }
}
