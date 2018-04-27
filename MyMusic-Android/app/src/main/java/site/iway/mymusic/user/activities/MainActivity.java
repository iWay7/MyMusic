package site.iway.mymusic.user.activities;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;

import site.iway.mymusic.R;
import site.iway.mymusic.user.fragments.MiniPlayerFragment;
import site.iway.mymusic.user.fragments.PlayListFragment;
import site.iway.mymusic.user.fragments.PlayingFragment;
import site.iway.mymusic.utils.Constants;

/**
 * Created by iWay on 2017/12/25.
 */

public class MainActivity extends BaseActivity {

    private ViewPager mViewPager;

    private PlayListFragment mPlayListFragment;
    private PlayingFragment mPlayingFragment;
    private MiniPlayerFragment mMiniPlayerFragment;

    private void setViews() {
        mViewPager = (ViewPager) findViewById(R.id.viewPager);
        mMiniPlayerFragment = (MiniPlayerFragment) mFragmentManager.findFragmentById(R.id.miniPlayerFragment);
        mPlayListFragment = new PlayListFragment();
        mPlayingFragment = new PlayingFragment();
        mViewPager.setOffscreenPageLimit(2);
        mViewPager.setAdapter(new FragmentPagerAdapter(mFragmentManager) {
            @Override
            public Fragment getItem(int position) {
                switch (position) {
                    case 0:
                        return mPlayListFragment;
                    case 1:
                        return mPlayingFragment;
                    default:
                        return null;
                }
            }

            @Override
            public int getCount() {
                return 2;
            }
        });
        mViewPager.setOnPageChangeListener(new OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                switch (position) {
                    case 0:
                        mMiniPlayerFragment.setVisibility(View.VISIBLE);
                        mMiniPlayerFragment.setAlpha(1.0f - positionOffset);
                        break;
                    case 1:
                        mMiniPlayerFragment.setVisibility(View.GONE);
                        break;
                }
            }

            @Override
            public void onPageSelected(int position) {
                // nothing
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                // nothing
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setViews();
    }

    @Override
    public void onEvent(String event, Object data) {
        super.onEvent(event, data);
        switch (event) {
            case Constants.EV_PLAY_LIST_VIEW:
                mViewPager.setCurrentItem(0);
                break;
        }
    }

    @Override
    public void onBackPressed() {
        finish();
    }

}
