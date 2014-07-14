package com.imran.collage.views;

import android.content.Context;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.imran.collage.R;

/**
 * Created by imran on 14/07/14.
 */
public class CollageView extends ViewGroup {

    int mViewWidth, mViewHeight;
    ImageContainer[] mImageContainers = new ImageContainer[5];

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
            frameView.setLayoutParams(new FrameLayout.LayoutParams(width, height));
            frameView.layout(left, top, right, bottom);
            imageView.setImageResource(R.drawable.ic_add);
            imageView.layout(0, 0, width, height);
        }
    }
}
