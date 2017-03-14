package vn.eazy.tagview.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Handler;
import android.support.v7.widget.AppCompatEditText;
import android.text.Html;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;

import com.labo.kaji.relativepopupwindow.RelativePopupWindow;

import vn.eazy.tagview.R;
import vn.eazy.tagview.adapter.SuggestionAdapter;
import vn.eazy.tagview.core.ActiveHashTag;
import vn.eazy.tagview.model.BaseData;

/**
 * Created by Harry on 2/10/17.
 */

public class TagEditTextView<T extends BaseData> extends AppCompatEditText implements Runnable, ViewTreeObserver.OnGlobalLayoutListener {
    private boolean isSupportHtml;
    private boolean isEnableHashTag;
    private boolean isEnableMention;
    private int colorHashtag;
    private int colorMention;
    private OnTypingListener onTypingListener;
    private PopupSuggestionWindow suggestionWindow;
    private SuggestionAdapter suggestionAdapter;
    private int[] posWindow;
    private boolean isTopAnchor;
    private Handler handler;
    private FrameLayout.LayoutParams lpPopup;

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

        getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    getViewTreeObserver().removeOnGlobalLayoutListener(this);
                } else {
                    getViewTreeObserver().removeGlobalOnLayoutListener(this);
                }
                suggestionWindow = new PopupSuggestionWindow(getContext(), getMeasuredWidth());
                if (suggestionAdapter != null && suggestionWindow.getAdapter() == null) {
                    suggestionWindow.setAdapter(suggestionAdapter);
                }
            }
        });
    }

    private int dpToPx(int dp, Context context) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        return Math.round(dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
    }

    public void showSuggestionDataPopup(boolean isTopAnchor) {
        this.isTopAnchor = isTopAnchor;
        if (suggestionWindow != null) {
            suggestionWindow.dismiss();
            suggestionWindow = null;
        }
        suggestionWindow = new PopupSuggestionWindow(getContext(), getMeasuredWidth());

        if (suggestionAdapter != null) {
            suggestionWindow.setAdapter(suggestionAdapter);
        }

        if (isTopAnchor) {
            if (posWindow == null) {
                posWindow = new int[2];
            }
            getLocationOnScreen(posWindow);
            suggestionWindow.showAtLocation(TagEditTextView.this, Gravity.TOP, 0, posWindow[1] - calcHeightOfPopup());
        } else {
            suggestionWindow.showAsDropDown(this, RelativePopupWindow.VerticalPosition.BELOW
                    , RelativePopupWindow.HorizontalPosition.CENTER);
        }
    }

    private int calcHeightOfPopup() {
        int sizeOfAllItems = suggestionAdapter.getHeightOfItem() * suggestionAdapter.getItemCount();
        int sizeCurrentOfPopup = dpToPx(150, getContext());
        if (lpPopup == null) {
            lpPopup = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        }
        if (sizeOfAllItems < sizeCurrentOfPopup) {
            suggestionWindow.getLayoutData().setLayoutParams(lpPopup);
            return sizeOfAllItems;
        } else {
            lpPopup.height = sizeCurrentOfPopup;
            suggestionWindow.getLayoutData().setLayoutParams(lpPopup);
//            suggestionWindow.getLayoutData().requestLayout();
            return sizeCurrentOfPopup;
        }
    }

    @Override
    public void onGlobalLayout() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            suggestionWindow.getContentView().getViewTreeObserver().removeOnGlobalLayoutListener(this);
        } else {
            suggestionWindow.getContentView().getViewTreeObserver().removeGlobalOnLayoutListener(this);
        }


    }

    @Override
    public boolean onKeyPreIme(int keyCode, KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK &&
                event.getAction() == KeyEvent.ACTION_UP) {
            if (isTopAnchor) {
                if (suggestionWindow != null) {
                    suggestionWindow.dismiss();
                }
                if (handler == null) {
                    handler = new Handler();
                }
                handler.postDelayed(this, 300);

            }
        }
        return super.dispatchKeyEvent(event);
    }

    @Override
    public void run() {
        showSuggestionDataPopup(true);
    }


    public void showSuggestionDataPopup() {
        showSuggestionDataPopup(false);
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

    public PopupSuggestionWindow getSuggestionWindow() {
        return suggestionWindow;
    }

    public void setHashTagClickListener(ActiveHashTag.OnHashTagClickListener hashTagClickListener) {
        this.hashTagClickListener = hashTagClickListener;
        activeHashTag.removeTextWatcher();
        activeHashTag.release();
        createActiveHashTag();
    }

    public void setOnTypingListener(OnTypingListener onTypingListener) {
        this.onTypingListener = onTypingListener;
    }


    public OnTypingListener getOnTypingListener() {
        return onTypingListener;
    }

    public void setSuggestionAdapter(SuggestionAdapter suggestionAdapter) {
        this.suggestionAdapter = suggestionAdapter;
        if (suggestionWindow != null) {
            suggestionWindow.setAdapter(suggestionAdapter);
        }
    }

    public void release() {
        if (suggestionWindow != null) {
            suggestionWindow.release();
            activeHashTag.release();
        }
    }
}
