package site.iway.mymusic.user.views;

import android.content.Context;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import site.iway.helpers.ExtendedBaseAdapter;
import site.iway.helpers.ExtendedImageView;
import site.iway.helpers.ExtendedTextView;
import site.iway.helpers.RPCInfo;
import site.iway.helpers.RPCListener;
import site.iway.helpers.ViewSwapper;
import site.iway.mymusic.R;
import site.iway.mymusic.net.RPCClient;
import site.iway.mymusic.net.req.PlayListReq;
import site.iway.mymusic.net.res.PlayListRes;
import site.iway.mymusic.utils.Player;
import site.iway.mymusic.utils.Song;

/**
 * Created by iWay on 2017/12/27.
 */

public class SongsAdapter extends ExtendedBaseAdapter<String> {

    private static List<String> sProcessingItems = new ArrayList<>();

    public SongsAdapter(Context context) {
        super(context);
    }

    private String mFilter;

    public void setSearchFilter(String filter) {
        mFilter = filter;
        notifyDataSetChanged();
    }

    private List<String> mFavSongs = new ArrayList<>();

    public void setPlayList(List<String> favSongs) {
        mFavSongs = favSongs;
        if (mFavSongs == null) {
            mFavSongs = new ArrayList<>();
        }
    }

    private boolean mSongsChanged;

    public boolean isSongsChanged() {
        return mSongsChanged;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = mLayoutInflater.inflate(R.layout.group_song, parent, false);
        }

        ExtendedTextView title = (ExtendedTextView) convertView.findViewById(R.id.title);
        ExtendedTextView artist = (ExtendedTextView) convertView.findViewById(R.id.artist);
        ExtendedImageView addPlayNow = (ExtendedImageView) convertView.findViewById(R.id.addPlayNow);
        ViewSwapper addToFav = (ViewSwapper) convertView.findViewById(R.id.addToFav);

        final String fileName = getItem(position);

        if (sProcessingItems.contains(fileName)) {
            addToFav.setDisplayedChild(0);
        } else if (mFavSongs.contains(fileName)) {
            addToFav.setDisplayedChild(2);
        } else {
            addToFav.setDisplayedChild(1);
        }

        if (addToFav.getDisplayedChild() == 1) {
            final OnClickListener onClickListener = new OnClickListener() {
                @Override
                public void onClick(View v) {
                    PlayListReq playListReq = new PlayListReq();
                    playListReq.action = PlayListReq.ACTION_ADD;
                    playListReq.fileNames = fileName;
                    playListReq.minDelayTime = 500;
                    sProcessingItems.add(fileName);
                    RPCInfo rpcInfo = RPCClient.doRequest(playListReq, new RPCListener() {
                        @Override
                        public void onRequestOK(RPCInfo rpcInfo, Object data) {
                            PlayListRes playListRes = (PlayListRes) data;
                            if (playListRes.resultCode == PlayListRes.OK) {
                                mFavSongs.add(fileName);
                                mSongsChanged = true;
                            }
                            sProcessingItems.remove(fileName);
                            notifyDataSetChanged();
                            if((Boolean) rpcInfo.getTag()) {
                                Player player = Player.getInstance();
                                player.addToPlayList(fileName);
                                player.playFile(fileName);
                            }
                        }

                        @Override
                        public void onRequestER(RPCInfo rpcInfo, Exception e) {
                            sProcessingItems.remove(fileName);
                            notifyDataSetChanged();
                        }
                    });
                    rpcInfo.setTag(v.getTag());
                    notifyDataSetChanged();
                }
            };
            OnLongClickListener onLongClickListener = new OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    v.setTag(true);
                    onClickListener.onClick(v);
                    return true;
                }
            };
            addToFav.setOnClickListener(onClickListener);
            addToFav.setOnLongClickListener(onLongClickListener);
            addToFav.setTag(false);
        } else if (addToFav.getDisplayedChild() == 2) {
            addToFav.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    PlayListReq playListReq = new PlayListReq();
                    playListReq.action = PlayListReq.ACTION_REMOVE;
                    playListReq.fileNames = fileName;
                    playListReq.minDelayTime = 500;
                    sProcessingItems.add(fileName);
                    RPCClient.doRequest(playListReq, new RPCListener() {
                        @Override
                        public void onRequestOK(RPCInfo rpcInfo, Object data) {
                            PlayListRes playListRes = (PlayListRes) data;
                            if (playListRes.resultCode == PlayListRes.OK) {
                                mFavSongs.remove(fileName);
                                mSongsChanged = true;
                            }
                            sProcessingItems.remove(fileName);
                            notifyDataSetChanged();
                        }

                        @Override
                        public void onRequestER(RPCInfo rpcInfo, Exception e) {
                            sProcessingItems.remove(fileName);
                            notifyDataSetChanged();
                        }
                    });
                    notifyDataSetChanged();
                }
            });
        } else {
            addToFav.setOnClickListener(null);
        }

        Song song = new Song(getItem(position));

        if (!TextUtils.isEmpty(song.name)) {
            title.setVisibility(View.VISIBLE);
            if (!TextUtils.isEmpty(mFilter)) {
                int index = song.name.indexOf(mFilter);
                if (index > -1) {
                    SpannableString spannableString = new SpannableString(song.name);
                    ForegroundColorSpan foregroundColorSpan = new ForegroundColorSpan(0xff507daf);
                    spannableString.setSpan(foregroundColorSpan, index, index + mFilter.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
                    title.setText(spannableString);
                } else {
                    title.setText(song.name);
                }
            } else {
                title.setText(song.name);
            }
        } else {
            title.setVisibility(View.GONE);
        }

        if (!TextUtils.isEmpty(song.artist)) {
            artist.setVisibility(View.VISIBLE);
            if (!TextUtils.isEmpty(mFilter)) {
                int index = song.artist.indexOf(mFilter);
                if (index > -1) {
                    SpannableString spannableString = new SpannableString(song.artist);
                    ForegroundColorSpan foregroundColorSpan = new ForegroundColorSpan(0xff507daf);
                    spannableString.setSpan(foregroundColorSpan, index, index + mFilter.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
                    artist.setText(spannableString);
                } else {
                    artist.setText(song.artist);
                }
            } else {
                artist.setText(song.artist);
            }
        } else {
            artist.setVisibility(View.GONE);
        }

        return convertView;
    }

}