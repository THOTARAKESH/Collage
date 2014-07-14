package com.imran.collage;

import android.app.Activity;
import android.app.DialogFragment;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;

import com.imran.collage.views.CollageView;

import java.io.IOException;

/**
 * Created by imran on 15/07/14.
 */
public class ImagePickerFragment extends DialogFragment {

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
        startActivityForResult(intent, 100);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == 100) {
                mCollageView.setBitmap(getBitmapFromCameraData(data));
            }
        } else {
            dismiss();
        }
    }

    public Bitmap getBitmapFromCameraData(Intent data) {
        Uri selectedImage = data.getData();
        try {
            return MediaStore.Images.Media.getBitmap(
                    getActivity().getContentResolver(), selectedImage);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
