package vn.eazy.tagview.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.Typeface;
import android.text.Html;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.widget.EditText;

import com.labo.kaji.relativepopupwindow.RelativePopupWindow;

import vn.eazy.tagview.R;
import vn.eazy.tagview.core.ActiveHashTag;

/**
 * Created by Harry on 2/10/17.
 */

public class TagEditTextView extends EditText {
    private boolean isSupportHtml;
    private boolean isEnableHashTag;
    private boolean isEnableMention;
    private int colorHashtag;
    private int colorMention;
    private OnTypingListener onTypingListener;
    private PopupSuggestionWindow suggestionWindow;

    public interface OnTypingListener {
        void onTypingHashTag(String hashTag);

        void onTypingMention(String mention);
    }

    private ActiveHashTag activeHashTag;

    private ActiveHashTag.OnHashTagClickListener hashTagClickListener;

    public TagEditTextView(Context context) {
        super(context);
        init(null);
    }

    public TagEditTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public TagEditTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    private void init(AttributeSet attrs) {
        if (attrs != null) {
            TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.TagTextView);
            isSupportHtml = typedArray.getBoolean(R.styleable.TagTextView_ttv_support_html, false);
            isEnableHashTag = typedArray.getBoolean(R.styleable.TagTextView_ttv_enable_hashtag, false);
            isEnableMention = typedArray.getBoolean(R.styleable.TagTextView_ttv_enable_mention, false);
            colorHashtag = typedArray.getColor(R.styleable.TagTextView_ttv_color_hashtag, Color.BLACK);
            colorMention = typedArray.getColor(R.styleable.TagTextView_ttv_color_mention, Color.BLACK);
            String font = typedArray.getString(R.styleable.TagTextView_ttv_font);
            if (!TextUtils.isEmpty(font)) {
                setTypeface(Typeface.createFromAsset(getContext().getAssets(), font));
            }
            typedArray.recycle();
        }
        setHighlightColor(Color.TRANSPARENT);
        createActiveHashTag();

        suggestionWindow = new PopupSuggestionWindow(getContext());
    }

    public void showSuggestionDataPopup() {
        if (suggestionWindow != null && !suggestionWindow.isShowing()) {
            suggestionWindow.showAsDropDown(this, RelativePopupWindow.VerticalPosition.BELOW
                    , RelativePopupWindow.HorizontalPosition.CENTER);
        }
    }

    public void dimissPopup() {
        if (suggestionWindow != null) {
            suggestionWindow.dismiss();
        }
    }

    private final void createActiveHashTag() {
        activeHashTag = ActiveHashTag.Factory.create(colorHashtag, colorMention, isEnableHashTag, isEnableMention, hashTagClickListener);
        activeHashTag.operate(this);
    }

    public void appendText(String content) {
        if (isSupportHtml) {
            setText(Html.fromHtml(content));
        } else {
            setText(content);
        }
    }

    public void setSupportHtml(boolean isSupportHtml) {
        this.isSupportHtml = isSupportHtml;
        invalidate();
    }

    public void enableHashtag(boolean isEnableHashtag) {
        this.isEnableHashTag = isEnableHashtag;
    }

    public void enableMention(boolean isEnableMention) {
        this.isEnableMention = isEnableMention;
    }

    public boolean isSupportHtml() {
        return isSupportHtml;
    }

    public boolean isEnableHashTag() {
        return isEnableHashTag;
    }

    public boolean isEnableMention() {
        return isEnableMention;
    }

    public void setColorForLinkable(int color) {

    }

    public void setHashTagClickListener(ActiveHashTag.OnHashTagClickListener hashTagClickListener) {
        this.hashTagClickListener = hashTagClickListener;
        activeHashTag.removeTextWatcher();
        createActiveHashTag();
    }

    public void setOnTypingListener(OnTypingListener onTypingListener) {
        this.onTypingListener = onTypingListener;
    }


    public OnTypingListener getOnTypingListener() {
        return onTypingListener;
    }
}