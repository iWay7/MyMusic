package site.iway.mymusic.user.views;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import site.iway.androidhelpers.ExtendedBaseAdapter;
import site.iway.androidhelpers.ExtendedTextView;
import site.iway.javahelpers.StringHelper;
import site.iway.mymusic.R;
import site.iway.mymusic.utils.Lyric;

/**
 * Created by iWay on 2017/12/27.
 */

public class LyricsAdapter extends ExtendedBaseAdapter<Lyric> {

    public LyricsAdapter(Context context) {
        super(context);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = mLayoutInflater.inflate(R.layout.group_lyric, parent, false);
        }

        ExtendedTextView title = (ExtendedTextView) convertView.findViewById(R.id.title);
        ExtendedTextView artist = (ExtendedTextView) convertView.findViewById(R.id.artist);

        if (StringHelper.nullOrEmpty(getItem(position).artist)) {
            title.setText(getItem(position).name);
            artist.setVisibility(View.GONE);
        } else {
            title.setText(getItem(position).name);
            artist.setText(getItem(position).artist);
            artist.setVisibility(View.VISIBLE);

        }

        return convertView;
    }

}