package com.imran.collage;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ShareActionProvider;

import com.crashlytics.android.Crashlytics;
import com.imran.collage.views.CollageView;

import java.io.File;


public class MyActivity extends Activity {


    ShareActionProvider mShareActionProvider;
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
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.my_activity, menu);

        MenuItem item = menu.findItem(R.id.action_share);
        mShareActionProvider = (ShareActionProvider) item.getActionProvider();
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_share) {
            mShareActionProvider.setShareIntent(shareCollage());
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        CollageView.ImageContainer[] imageContainers = mCollageView.getImageContainers();
        for (CollageView.ImageContainer im : imageContainers) {
            if (im.imageUri != null) {
                outState.putParcelable(String.valueOf(im.position), im.imageUri);
            }
        }
        super.onSaveInstanceState(outState);
    }

    public Intent shareCollage() {
        File file = Utils.getTempFile("collage.jpg");
        mCollageView.saveCollage(file);
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file));
        return intent;
    }

}
