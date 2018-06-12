package site.iway.mymusic.user.activities;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

import site.iway.androidhelpers.BitmapCache;
import site.iway.androidhelpers.BitmapInfo;
import site.iway.androidhelpers.BitmapInfoListener;
import site.iway.androidhelpers.BitmapSource;
import site.iway.androidhelpers.BitmapSourceURL;
import site.iway.androidhelpers.ImageViewer;
import site.iway.androidhelpers.ViewSwapper;
import site.iway.mymusic.R;

public class ViewImageActivity extends BaseActivity {

    public static final String TITLE = "TITLE";
    public static final String IMAGE_URL = "URL";

    private static final int INDEX_LOADING = 0;
    private static final int INDEX_LIST = 1;
    private static final int INDEX_ERROR = 2;

    private ViewSwapper mViewSwapper;
    private ImageViewer mImageViewer;

    private Bitmap mBitmap;

    public void setViews() {
        mTitleBarText.setText(mIntent.getStringExtra(TITLE));
        mTitleBarBack.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        mViewSwapper = (ViewSwapper) findViewById(R.id.viewSwapper);
        mImageViewer = (ImageViewer) findViewById(R.id.imageViewer);

        BitmapSource bitmapSource = new BitmapSourceURL(mIntent.getStringExtra(IMAGE_URL));
        BitmapInfoListener bitmapInfoListener = new BitmapInfoListener() {
            @Override
            public void onBitmapInfoChange(BitmapInfo bitmapInfo) {
                switch (bitmapInfo.getProgress()) {
                    case BitmapInfo.GET_BITMAP:
                        mBitmap = Bitmap.createBitmap(bitmapInfo.getBitmap());
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                mImageViewer.setBitmap(mBitmap);
                                mViewSwapper.setDisplayedChild(INDEX_LIST);
                            }
                        });
                        break;
                    case BitmapInfo.GET_ERROR:
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                mViewSwapper.setDisplayedChild(INDEX_ERROR);
                            }
                        });
                        break;
                }
            }
        };
        BitmapCache.get(bitmapSource, bitmapInfoListener);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_image);
        setViews();
    }

    @Override
    public void onBackPressed() {
        finish();
    }

}
