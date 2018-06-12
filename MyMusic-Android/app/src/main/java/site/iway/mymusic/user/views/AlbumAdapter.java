package site.iway.mymusic.user.views;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import site.iway.androidhelpers.BitmapView;
import site.iway.androidhelpers.ExtendedBaseAdapter;
import site.iway.androidhelpers.UnitHelper;
import site.iway.androidhelpers.WindowHelper;
import site.iway.mymusic.R;

public class AlbumAdapter extends ExtendedBaseAdapter<String> {

    public AlbumAdapter(Context context) {
        super(context);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = mLayoutInflater.inflate(R.layout.group_album, parent, false);
            convertView.getLayoutParams().width = (WindowHelper.getScreenWidth(mContext) - UnitHelper.dipToPxInt(10)) / 2;
            convertView.getLayoutParams().height = WindowHelper.getScreenWidth(mContext) / 2;
        }

        BitmapView bitmapView = (BitmapView) convertView.findViewById(R.id.bitmapView);
        bitmapView.loadFromURLSource(getItem(position));

        return convertView;
    }

}
