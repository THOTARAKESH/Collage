package com.imran.collage.views;

import android.app.Activity;
import android.content.ClipData;
import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.DragEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import com.imran.collage.ImagePickerFragment;
import com.imran.collage.R;

import java.io.IOException;

/**
 * Created by imran on 14/07/14.
 */
public class CollageView extends ViewGroup implements View.OnClickListener, View.OnLongClickListener, View.OnDragListener {

    private final int IMAGE_MAX_SIZE = 1000;
    private final String FRAGMENT_TAG = "gallery_fragment";
    private final int MIN_DRAG_DISTANCE = 25;
    private final int NUM_COLUMNS = 2;
    private final int NUM_ROWS = 3;

    int mViewWidth, mViewHeight;
    ImageContainer[] mImageContainers = new ImageContainer[5];
    ImagePickerFragment mImagePickerFragment;
    ImageContainer mSelectedImageContainer;
    boolean mPortrait;

    int mColumnSpacing = 0, mRowSpacing = 0;

    public CollageView(Context context) {
        this(context, null);
    }

    public CollageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CollageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray array = getContext().obtainStyledAttributes(attrs, R.styleable.CollageView, defStyleAttr, 0);
        mColumnSpacing = array.getDimensionPixelSize(R.styleable.CollageView_columnSpacing, 0);
        mRowSpacing = array.getDimensionPixelSize(R.styleable.CollageView_rowSpacing, 0);
        init();
    }

    /**
     * Measure screen sizes
     * Initialize objects
     */
    void init() {
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        mViewWidth = metrics.widthPixels;
        mViewHeight = metrics.heightPixels;
        mImagePickerFragment = ImagePickerFragment.getInstance(this);
        int count = 0;
        for (int i = 0; i < NUM_ROWS; i++) {
            for (int j = 0; j < NUM_COLUMNS; j++) {
                if (count >= 4) {
                    mImageContainers[count] = new ImageContainer(count, j, i);
                    break;
                }
                mImageContainers[count] = new ImageContainer(count, j, i);
                count++;
            }
        }
    }

    /**
     * Layout Views to be drawn at particular positions.
     *
     * @param changed
     * @param l
     * @param t
     * @param r
     * @param b
     */
    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {

        int width = r - l;
        int height = b - t;

        int o = getResources().getConfiguration().orientation;
        mPortrait = o == Configuration.ORIENTATION_PORTRAIT;

        for (ImageContainer im : mImageContainers) {
            if (im != null) {
                im.setLayout(width - (mColumnSpacing * (NUM_COLUMNS - 1)), height - (mRowSpacing * (NUM_ROWS - 1)));
            }
        }

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(MeasureSpec.getSize(mViewWidth),
                MeasureSpec.getSize(mViewHeight));
    }

    /**
     * Override Drag Event.
     * Alpha of the image is changed while dragging.
     * Switch images on drag finished.
     *
     * @param view
     * @param dragEvent
     * @return
     */
    @Override
    public boolean onDrag(View view, DragEvent dragEvent) {
        int action = dragEvent.getAction();
        switch (action) {
            case DragEvent.ACTION_DRAG_STARTED:
                break;
            case DragEvent.ACTION_DRAG_ENTERED:
                view.setAlpha((float) 0.7);
                break;
            case DragEvent.ACTION_DRAG_EXITED:
                view.setAlpha((float) 1.0);
                break;
            case DragEvent.ACTION_DROP:
                final View v = (View) dragEvent.getLocalState();
                ViewGroup src = (ViewGroup) v.getParent();
                src.removeView(v);

                final FrameLayout dest = (FrameLayout) view;

                View im = dest.getChildAt(0);
                if (im != null) {
                    dest.removeView(im);

                    ImageContainer iC = (ImageContainer) src.getTag();
                    iC.imageView = (ImageView) im;
                    src.addView(im);

                }
                ImageContainer iC1 = (ImageContainer) dest.getTag();
                iC1.imageView = (ImageView) v;

                dest.addView(v);
                v.setVisibility(View.VISIBLE);

                if (im != null) {
                    Animation animation = AnimationUtils.loadAnimation(getContext(), R.anim.anim);
                    // Disable animation temp
                    //im.startAnimation(animation);
                }

                break;
            case DragEvent.ACTION_DRAG_ENDED:
                break;
            default:
                break;
        }
        return true;
    }


    /**
     * Onclick select image from Gallery
     *
     * @param view
     */
    @Override
    public void onClick(View view) {
        mSelectedImageContainer = (ImageContainer) ((View) view.getParent()).getTag();

        if (getContext() instanceof Activity) {
            if (!mImagePickerFragment.isAdded()) {
                mImagePickerFragment.show(((Activity) getContext()).getFragmentManager(), FRAGMENT_TAG);
            }
        }
    }


    public ImageContainer getSelectedImageContainer(){
        return mSelectedImageContainer;
    }
    /**
     */
    public void setBitmap(Uri imageUri) {
        mImagePickerFragment.dismiss();
        mSelectedImageContainer.updateImage(imageUri);
    }

    public Bitmap getBitmap(Uri imageUri) {
        try {
            AssetFileDescriptor fileDescriptor = getContext().getContentResolver().openAssetFileDescriptor(imageUri, "r");
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeFileDescriptor(fileDescriptor.getFileDescriptor(), null, options);

            int scale = 1;
            if (options.outHeight > IMAGE_MAX_SIZE || options.outWidth > IMAGE_MAX_SIZE) {
                scale = (int) Math.pow(2, (int) Math.ceil(Math.log(IMAGE_MAX_SIZE /
                        (double) Math.max(options.outHeight, options.outWidth)) / Math.log(0.5)));
            }

            options.inSampleSize = scale;
            options.inJustDecodeBounds = false;

            return BitmapFactory.decodeFileDescriptor(fileDescriptor.getFileDescriptor(), null, options);
        } catch (IOException e) {
            Toast.makeText(getContext(), "Image not found", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
        return null;
    }

    /**
     * OnLongClick enable drag
     *
     * @param view
     * @return
     */
    @Override
    public boolean onLongClick(View view) {
        ClipData data = ClipData.newPlainText("", "");
        DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(view);
        view.startDrag(data, shadowBuilder, view, 0);
        view.setVisibility(View.INVISIBLE);
        return false;
    }

    /*
    Replacing OnClick and OnLongClick in place of OnTouch.
    Precise calculations are needed to achieve onTouch and Drag.
     */

 /*
     float eventX, eventY;

  @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        int action = motionEvent.getAction();

        switch (action) {
            case MotionEvent.ACTION_DOWN:
                eventX = motionEvent.getX();
                eventY = motionEvent.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                float x = motionEvent.getX();
                float y = motionEvent.getY();
                float x1 = eventX + x;
                float y1 = eventY + y;
                double res = Math.sqrt(((int)x1^2) + ((int)y1^2));
                if(res > MIN_DRAG_DISTANCE){
                    ClipData data = ClipData.newPlainText("", "");
                    DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(view);
                    view.startDrag(data, shadowBuilder, view, 0);
                    view.setVisibility(View.INVISIBLE);
                } else {
                    return false;
                }
                break;
            case MotionEvent.ACTION_UP:
                break;
        }

        return true;
    }
*/

    public ImageContainer[] getImageContainers() {
        return mImageContainers;
    }

    public class ImageContainer {

        public int position;
        int rowPosition, columnPosition;

        int left, top, right, bottom;

        FrameLayout frameView;
        public ImageView imageView;
        public Uri imageUri;

        public ImageContainer(int position, int columnPosition, int rowPosition) {
            this.position = position;
            this.rowPosition = rowPosition;
            this.columnPosition = columnPosition;

            frameView = new FrameLayout(getContext());
            addView(frameView);
            imageView = new ImageView(frameView.getContext());
            imageView.setImageResource(R.drawable.ic_add);
            imageView.setBackgroundColor(Color.DKGRAY + position * 100);
            imageView.setScaleType(ImageView.ScaleType.CENTER);
            frameView.addView(imageView);
        }

        private void setLayout(int width, int height) {

            int cHeight = height / NUM_ROWS;
            int cWidth = width;
            if (mPortrait) {
                if (position != 4) {
                    cWidth = width / NUM_COLUMNS;
                }

            } else {
                cWidth = cHeight;
                if (position % 2 != 0) {
                    cWidth = width - cHeight;
                }
            }

            if (position % NUM_COLUMNS == 0) {
                this.left = mColumnSpacing;
            } else {
                this.left = cHeight + mColumnSpacing;
            }

            this.top = (rowPosition * cHeight) + mRowSpacing;

            right = (cWidth * (columnPosition + 1));
            bottom = (cHeight * (rowPosition + 1));
            //frameView.setBackgroundColor(Color.RED);
            frameView.layout(left, top, right, bottom);
            frameView.setOnDragListener(CollageView.this);
            imageView.layout(0, 0, right - left, bottom - top);
            imageView.setOnClickListener(CollageView.this);
            imageView.setOnLongClickListener(CollageView.this);
            frameView.setTag(this);
        }

        public void updateImage(Uri imageUri) {
            if (imageUri != null) {
                this.imageUri = imageUri;
                this.imageView.setImageBitmap(getBitmap(imageUri));
                this.imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            }
        }
    }
}
