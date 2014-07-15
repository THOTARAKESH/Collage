package com.imran.collage;

import android.app.Activity;
import android.app.DialogFragment;
import android.content.Intent;
import android.os.Bundle;

import com.imran.collage.views.CollageView;

/**
 * Created by imran on 15/07/14.
 */
public class ImagePickerFragment extends DialogFragment {

    private final int GALLERY_REQUEST_CODE = 11011;
    CollageView mCollageView;

    public static ImagePickerFragment getInstance(CollageView view) {
        ImagePickerFragment fragment = new ImagePickerFragment();
        fragment.setStyle(STYLE_NO_FRAME, android.R.style.Theme_NoDisplay);
        fragment.mCollageView = view;
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, GALLERY_REQUEST_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == GALLERY_REQUEST_CODE) {
                mCollageView.setBitmap(data.getData());
            }
        } else {
            dismiss();
        }
    }

}
