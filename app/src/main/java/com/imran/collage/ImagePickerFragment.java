package com.imran.collage;

import android.app.Activity;
import android.app.DialogFragment;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;

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
        CollageView.ImageContainer selectedContainer = mCollageView.getSelectedImageContainer();
        Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        intent.putExtra("crop", "true");
        intent.putExtra("outputX", selectedContainer.imageView.getWidth());
        intent.putExtra("outputY", selectedContainer.imageView.getHeight());
        intent.putExtra(MediaStore.EXTRA_OUTPUT, getTempUri());
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
        startActivityForResult(intent, GALLERY_REQUEST_CODE);
    }

    private Uri getTempUri() {
        return Uri.fromFile(Utils.getTempFile("temp.jpg"));
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == GALLERY_REQUEST_CODE) {
                mCollageView.setBitmap(getTempUri());
                Utils.getTempFile("temp.jpg").delete();
            }
        } else {
            dismiss();
        }
    }

}
