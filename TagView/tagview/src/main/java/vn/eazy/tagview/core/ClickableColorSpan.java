package vn.eazy.tagview.core;


import android.support.annotation.ColorInt;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.style.ClickableSpan;
import android.view.View;
import android.widget.TextView;

public class ClickableColorSpan extends ClickableSpan {

    private OnHashTagClickListener onHashTagClickListener;

    public interface OnHashTagClickListener {
        void onHashTagClicked(String hashTag);

        void onMentionClicked(String mention);
    }

    private boolean isHashTag;
    private final int color;

    public ClickableColorSpan(@ColorInt int color, boolean isHashTag, OnHashTagClickListener listener) {
        this.isHashTag = isHashTag;
        this.color = color;
        onHashTagClickListener = listener;

        if (onHashTagClickListener == null) {
            throw new RuntimeException("click listener is null");
        }

    }

    @Override
    public void updateDrawState(TextPaint ds) {
        ds.setColor(color);
    }

    @Override
    public void onClick(View widget) {
        CharSequence text = ((TextView) widget).getText();

        Spanned spanned = (Spanned) text;
        int start = spanned.getSpanStart(this);
        int end = spanned.getSpanEnd(this);

        if(isHashTag) {
            onHashTagClickListener.onHashTagClicked(text.subSequence(start + 1/*skip "#" sign*/, end).toString());
        }else{
            onHashTagClickListener.onMentionClicked(text.subSequence(start + 1/*skip "#" sign*/, end).toString());
        }

    }
}
