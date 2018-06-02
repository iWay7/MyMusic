package site.iway.mymusic.user.views;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.LinearInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.List;

import site.iway.androidhelpers.ExtendedImageView;
import site.iway.androidhelpers.ExtendedLinearLayout;
import site.iway.androidhelpers.ExtendedTextView;
import site.iway.mymusic.R;
import site.iway.mymusic.utils.PlayList;
import site.iway.mymusic.utils.PlayTask;
import site.iway.mymusic.utils.Player;
import site.iway.mymusic.utils.Song;

/**
 * Created by iWay on 2017/12/26.
 */

public class PlayListAdapter extends BaseAdapter {

    private LayoutInflater mLayoutInflater;
    private PlayList mPlayList;

    public PlayListAdapter(Context context) {
        mLayoutInflater = LayoutInflater.from(context);
        Player player = Player.getInstance();
        mPlayList = player.getPlayList();
    }

    public void setData(List<String> data) {
        mPlayList.replace(data);
        mPlayList.resort();
        notifyDataSetChanged();
    }

    public void resort() {
        mPlayList.resort();
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return mPlayList.size();
    }

    @Override
    public String getItem(int position) {
        return mPlayList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    private boolean mInSelectionMode = false;
    private List<String> mSelectedItems = new ArrayList<>();

    public void setInSelectionMode(boolean inSelectionMode) {
        mInSelectionMode = inSelectionMode;
        mSelectedItems.clear();
        notifyDataSetChanged();
    }

    public boolean isInSelectionMode() {
        return mInSelectionMode;
    }

    public boolean allSelected() {
        return mSelectedItems.size() == getCount();
    }

    public void selectAll(boolean selected) {
        mSelectedItems.clear();
        if (selected) {
            mSelectedItems.addAll(mPlayList);
        }
        notifyDataSetChanged();
    }

    public void addSelectedItem(int position) {
        mSelectedItems.add(getItem(position));
    }

    public void removeSelectedItem(int position) {
        mSelectedItems.remove(getItem(position));
    }

    public List<String> getSelectedItems() {
        return mSelectedItems;
    }

    public void removeItemsFromSelected() {
        mPlayList.removeAll(mSelectedItems);
        notifyDataSetChanged();
    }

    private int mFirstVisibleItem;
    private int mLastVisibleItem;

    public void setFirstVisibleItem(int firstVItem) {
        mFirstVisibleItem = firstVItem;
    }

    public void setLastVisibleItem(int lastVItem) {
        mLastVisibleItem = lastVItem;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = mLayoutInflater.inflate(R.layout.group_favorites_item, parent, false);
        }

        ExtendedImageView playingIndicator = (ExtendedImageView) convertView.findViewById(R.id.playingIndicator);
        ExtendedLinearLayout songInfoContainer = (ExtendedLinearLayout) convertView.findViewById(R.id.songInfoContainer);
        ExtendedTextView title = (ExtendedTextView) convertView.findViewById(R.id.title);
        ExtendedTextView splitter = (ExtendedTextView) convertView.findViewById(R.id.splitter);
        ExtendedTextView artist = (ExtendedTextView) convertView.findViewById(R.id.artist);
        ExtendedImageView itemSelection = (ExtendedImageView) convertView.findViewById(R.id.itemSelection);

        String fileName = getItem(position);
        Song song = new Song(fileName);
        title.setText(song.name);
        splitter.setText("  -  ");
        artist.setText(song.artist);
        Player player = Player.getInstance();
        PlayTask playTask = player.getPlayTask();
        if (playTask != null && fileName.equals(playTask.getMusicFileName())) {
            int oldVisibility = playingIndicator.getVisibility();
            playingIndicator.setVisibility(View.VISIBLE);
            title.setTextColor(0xffd33a31);
            splitter.setTextColor(0xffd33a31);
            artist.setTextColor(0xffd33a31);
            if (oldVisibility != View.VISIBLE && position >= mFirstVisibleItem && position <= mLastVisibleItem) {
                AlphaAnimation alphaAnimation = new AlphaAnimation(0, 1);
                alphaAnimation.setStartOffset(100);
                alphaAnimation.setDuration(200);
                playingIndicator.startAnimation(alphaAnimation);
                int playingIndicatorWidth = playingIndicator.getWidth();
                LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) playingIndicator.getLayoutParams();
                int playingIndicatorRightMargin = layoutParams.rightMargin;
                TranslateAnimation translateAnimation = new TranslateAnimation(
                        Animation.ABSOLUTE, -(playingIndicatorWidth + playingIndicatorRightMargin),
                        Animation.ABSOLUTE, 0,
                        Animation.ABSOLUTE, 0,
                        Animation.ABSOLUTE, 0
                );
                translateAnimation.setInterpolator(new LinearInterpolator());
                translateAnimation.setDuration(150);
                songInfoContainer.startAnimation(translateAnimation);
            }
        } else {
            playingIndicator.setVisibility(View.GONE);
            title.setTextColor(0xff313131);
            splitter.setTextColor(0xff7a7a7a);
            artist.setTextColor(0xff7a7a7a);
        }

        int oldVisibility = itemSelection.getVisibility();
        int newVisibility = mInSelectionMode ? View.VISIBLE : View.GONE;
        itemSelection.setVisibility(newVisibility);
        if (mSelectedItems.contains(fileName)) {
            itemSelection.setImageResource(R.drawable.icon_song_select_selected);
        } else {
            itemSelection.setImageResource(R.drawable.icon_song_select_normal);
        }
        if (newVisibility != oldVisibility &&
                oldVisibility != View.VISIBLE &&
                position >= mFirstVisibleItem &&
                position <= mLastVisibleItem) {
            AlphaAnimation alphaAnimation = new AlphaAnimation(0, 1);
            alphaAnimation.setInterpolator(new LinearInterpolator());
            alphaAnimation.setDuration(300);
            TranslateAnimation translateAnimation = new TranslateAnimation(
                    Animation.RELATIVE_TO_SELF, 1,
                    Animation.ABSOLUTE, 0,
                    Animation.ABSOLUTE, 0,
                    Animation.ABSOLUTE, 0
            );
            translateAnimation.setInterpolator(new LinearInterpolator());
            translateAnimation.setDuration(300);
            AnimationSet animationSet = new AnimationSet(false);
            animationSet.addAnimation(alphaAnimation);
            animationSet.addAnimation(translateAnimation);
            itemSelection.startAnimation(animationSet);
        } else if (newVisibility != oldVisibility &&
                oldVisibility != View.GONE &&
                position >= mFirstVisibleItem &&
                position <= mLastVisibleItem) {
            AlphaAnimation alphaAnimation = new AlphaAnimation(1, 0);
            alphaAnimation.setInterpolator(new LinearInterpolator());
            alphaAnimation.setDuration(300);
            TranslateAnimation translateAnimation = new TranslateAnimation(
                    Animation.ABSOLUTE, 0,
                    Animation.RELATIVE_TO_SELF, 1,
                    Animation.ABSOLUTE, 0,
                    Animation.ABSOLUTE, 0
            );
            translateAnimation.setInterpolator(new LinearInterpolator());
            translateAnimation.setDuration(300);
            AnimationSet animationSet = new AnimationSet(false);
            animationSet.addAnimation(alphaAnimation);
            animationSet.addAnimation(translateAnimation);
            itemSelection.startAnimation(animationSet);
        }

        return convertView;
    }

}
