package com.imran.collage.views;

import android.app.Activity;
import android.content.ClipData;
import android.content.Context;
import android.graphics.Bitmap;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.DragEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.imran.collage.ImagePickerFragment;
import com.imran.collage.R;

/**
 * Created by imran on 14/07/14.
 */
public class CollageView extends ViewGroup implements View.OnClickListener, View.OnLongClickListener, View.OnDragListener {

    private final int MIN_DRAG_DISTANCE = 25;
    int mViewWidth, mViewHeight;
    ImageContainer[] mImageContainers = new ImageContainer[5];
    ImagePickerFragment mImagePickerFragment;
    ImageView mSelectedImageView;

    public CollageView(Context context) {
        this(context, null);
    }

    public CollageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CollageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }


    void init() {
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        mViewWidth = metrics.widthPixels - getPaddingLeft() - getPaddingRight();
        mViewHeight = (int) (metrics.heightPixels * 0.75);
        mImagePickerFragment = ImagePickerFragment.getInstance(this);
        int count = 0;
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 2; j++) {
                if (count >= 4) {
                    mImageContainers[count] = new ImageContainer(count, j, i);
                    break;
                }
                mImageContainers[count] = new ImageContainer(count, j, i);
                count++;
            }
        }
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {

        int width = r - l;
        int height = b - t;

        for (ImageContainer im : mImageContainers) {
            if (im != null) {
                im.setLayout(width, height);
            }
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(MeasureSpec.getSize(mViewWidth),
                MeasureSpec.getSize(mViewHeight));
    }

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
                View v = (View) dragEvent.getLocalState();
                ViewGroup owner = (ViewGroup) v.getParent();
                owner.removeView(v);
                FrameLayout container = (FrameLayout) view;

                View im = ((FrameLayout) view).getChildAt(0);
                ((FrameLayout) view).removeView(im);
                if (im != null) {
                    im.layout(0, 0, owner.getWidth(), owner.getHeight());
                    im.setLayoutParams(owner.getLayoutParams());
                    owner.addView(im);
                }
                v.layout(0, 0, container.getWidth(), container.getHeight());
                v.setLayoutParams(container.getLayoutParams());
                container.addView(v);
                v.setVisibility(View.VISIBLE);
                break;
            case DragEvent.ACTION_DRAG_ENDED:
                view.setAlpha((float) 1.0);
            default:
                break;
        }
        return true;
    }


    @Override
    public void onClick(View view) {

        mSelectedImageView = (ImageView) view;
        if (getContext() instanceof Activity) {
            mImagePickerFragment.show(((Activity) getContext()).getFragmentManager(), "dialog");
        }
    }

    public void setBitmap(Bitmap bitmap) {
        mImagePickerFragment.dismiss();
        mSelectedImageView.setImageBitmap(bitmap);
        mSelectedImageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
    }

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

    public class ImageContainer {

        int position;
        int rowPosition, columnPosition;

        int left, top, right, bottom;

        FrameLayout frameView;
        ImageView imageView;

        public ImageContainer(int position, int columnPosition, int rowPosition) {
            this.position = position;
            this.rowPosition = rowPosition;
            this.columnPosition = columnPosition;

            frameView = new FrameLayout(getContext());
            addView(frameView);
            imageView = new ImageView(frameView.getContext());
            imageView.setImageResource(R.drawable.ic_add);
            frameView.addView(imageView);
        }

        private void setLayout(int width, int height) {

            if (position != 4) {
                width = width / 2;
            }
            height = height / 3;

            if (position % 2 == 0) {
                this.left = 0;
            } else {
                this.left = width;
            }
            this.top = (rowPosition * height);
            right = width * (columnPosition + 1);
            bottom = height * (rowPosition + 1);
            frameView.layout(left, top, right, bottom);
            frameView.setOnDragListener(CollageView.this);
            imageView.layout(0, 0, width, height);
            imageView.setOnClickListener(CollageView.this);
            imageView.setOnLongClickListener(CollageView.this);
        }
    }
}
