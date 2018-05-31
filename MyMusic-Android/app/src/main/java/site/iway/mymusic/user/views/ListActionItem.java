package site.iway.mymusic.user.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import site.iway.androidhelpers.ExtendedImageView;
import site.iway.androidhelpers.ExtendedTextView;
import site.iway.mymusic.R;

public class ListActionItem extends FrameLayout {

    public ListActionItem(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initViews(context, attrs);
    }

    public ListActionItem(Context context, AttributeSet attrs) {
        super(context, attrs);
        initViews(context, attrs);
    }

    public ListActionItem(Context context) {
        super(context);
        initViews(context, null);
    }

    private ViewGroup mRootView;
    private ExtendedTextView mTextViewTitle;
    private ExtendedTextView mTextViewDesc;
    private ExtendedImageView mImageViewArrow;

    private void initViews(Context context, AttributeSet attrs) {
        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mRootView = (ViewGroup) layoutInflater.inflate(R.layout.view_list_action_item, this, false);
        mTextViewTitle = (ExtendedTextView) mRootView.findViewById(R.id.textViewTitle);
        mTextViewDesc = (ExtendedTextView) mRootView.findViewById(R.id.textViewDesc);
        mImageViewArrow = (ExtendedImageView) mRootView.findViewById(R.id.imageViewArrow);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.ListActionItem);
        mTextViewTitle.setText(a.getString(R.styleable.ListActionItem_ListActionItemTitle));
        mTextViewTitle.setTextColor(a.getColor(R.styleable.ListActionItem_ListActionItemTitleColor, 0xFF333333));
        mTextViewTitle.setTextSize(TypedValue.COMPLEX_UNIT_PX, a.getDimension(R.styleable.ListActionItem_ListActionItemTitleTextSize, mTextViewTitle.getTextSize()));
        mTextViewDesc.setText(a.getString(R.styleable.ListActionItem_ListActionItemDesc));
        mTextViewDesc.setTextColor(a.getColor(R.styleable.ListActionItem_ListActionItemDescColor, 0xFF999999));
        mTextViewDesc.setTextSize(TypedValue.COMPLEX_UNIT_PX, a.getDimension(R.styleable.ListActionItem_ListActionItemDescTextSize, mTextViewDesc.getTextSize()));
        mImageViewArrow.setVisibility(a.getBoolean(R.styleable.ListActionItem_ListActionItemShowArrow, true) ? View.VISIBLE : View.GONE);
        Drawable drawable = a.getDrawable(R.styleable.ListActionItem_ListActionItemArrow);
        if (drawable != null) {
            mImageViewArrow.setImageDrawable(drawable);
        }
        a.recycle();
        addView(mRootView);
    }

    public void setTitle(CharSequence title) {
        mTextViewTitle.setText(title);
    }

    public void setDesc(CharSequence desc) {
        mTextViewDesc.setText(desc);
    }

    public void showArrow() {
        mImageViewArrow.setVisibility(View.VISIBLE);
    }

    public void hideArrow() {
        mImageViewArrow.setVisibility(View.GONE);
    }

}
