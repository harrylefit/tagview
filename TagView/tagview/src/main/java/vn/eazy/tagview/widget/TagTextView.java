package vn.eazy.tagview.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.support.v7.widget.AppCompatTextView;
import android.text.DynamicLayout;
import android.text.Html;
import android.text.StaticLayout;
import android.text.TextUtils;
import android.util.AttributeSet;

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.List;

import vn.eazy.tagview.R;
import vn.eazy.tagview.core.ActiveHashTag;

/**
 * Created by Harry on 2/10/17.
 */

public class TagTextView extends AppCompatTextView {
    private boolean isSupportHtml;
    private boolean isEnableHashtag;
    private boolean isEnableMention;
    private int colorHashtag;
    private int colorMention;

    private ActiveHashTag activeHashTag;

    private ActiveHashTag.OnHashTagClickListener hashTagClickListener;

    public TagTextView(Context context) {
        super(context);
        init(null);
    }

    public TagTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public TagTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    private void init(AttributeSet attrs) {
        if (attrs != null) {
            TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.TagTextView);
            isSupportHtml = typedArray.getBoolean(R.styleable.TagTextView_ttv_support_html, false);
            isEnableHashtag = typedArray.getBoolean(R.styleable.TagTextView_ttv_enable_hashtag, false);
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
    }

    private final void createActiveHashTag() {
        activeHashTag = ActiveHashTag.Factory.create(colorHashtag, colorMention, isEnableHashtag, isEnableMention, hashTagClickListener);
        activeHashTag.operate(this);
    }

    public void appendText(String content) {
        if (isSupportHtml) {
            setText(Html.fromHtml(content));
        } else {
            setText(content);
        }
    }

    @Deprecated
    @Override
    public void setText(CharSequence text, BufferType type) {
        super.setText(text, type);
    }

    public void setSupportHtml(boolean isSupportHtml) {
        this.isSupportHtml = isSupportHtml;
        invalidate();
    }

    public void enableHashtag(boolean isEnableHashtag) {
        this.isEnableHashtag = isEnableHashtag;
    }

    public void enableMention(boolean isEnableMention) {
        this.isEnableMention = isEnableMention;
    }

    public boolean isSupportHtml() {
        return isSupportHtml;
    }

    public boolean isEnableHashtag() {
        return isEnableHashtag;
    }

    public boolean isEnableMention() {
        return isEnableMention;
    }

    public void setColorForLinkable(int color) {

    }

    public List<String> getAllLinks(boolean isHashTag) {
        if (activeHashTag == null) {
            return Collections.emptyList();
        }
        return activeHashTag.getAllLinks(isHashTag);
    }

    public void setHashTagClickListener(ActiveHashTag.OnHashTagClickListener hashTagClickListener) {
        this.hashTagClickListener = hashTagClickListener;
        createActiveHashTag();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        StaticLayout layout = null;
        Field field = null;
        try {
            Field staticField = DynamicLayout.class.getDeclaredField("sStaticLayout");
            staticField.setAccessible(true);
            layout = (StaticLayout) staticField.get(DynamicLayout.class);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        if (layout != null) {
            try {
                field = StaticLayout.class.getDeclaredField("mMaximumVisibleLineCount");
                field.setAccessible(true);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    field.setInt(layout, getMaxLines());
                } else {
                    field.setInt(layout, 1);
                }
            } catch (NoSuchFieldException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (layout != null && field != null) {
            try {
                field.setInt(layout, Integer.MAX_VALUE);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }
}
