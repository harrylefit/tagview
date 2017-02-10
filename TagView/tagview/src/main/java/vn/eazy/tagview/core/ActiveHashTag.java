package vn.eazy.tagview.core;

import android.text.Editable;
import android.text.Spannable;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.text.style.CharacterStyle;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import vn.eazy.tagview.widget.TagEditTextView;

public final class ActiveHashTag implements ClickableColorSpan.OnHashTagClickListener {
    private final String TAG = ActiveHashTag.class.getSimpleName();
    private OnHashTagClickListener onHashTagClickListener;

    public interface OnHashTagClickListener {
        void onHashTagClicked(String hashTag);
    }

    private final List<Character> hashTagCharsList;
    private TextView textView;
    private int colorHashtag;
    private int colorMention;

    private boolean isEnableHashtag;
    private boolean isEnableMention;

    private String currContent = "";

    private enum TYPE {
        HASHTAG, MENTION;
    }

    public static final class Factory {

        private Factory() {
        }

        public static ActiveHashTag create(int colorHashtag, int colorMention, boolean isEnableHashtag, boolean isEnableMention, OnHashTagClickListener listener) {
            return new ActiveHashTag(colorHashtag, colorMention, listener, isEnableHashtag, isEnableMention, null);
        }
    }

    private final TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence text, int start, int before, int count) {
            if (text.length() > 0) {
                resetColorizeText(text);
                currContent = text.toString();
            }
        }

        @Override
        public void afterTextChanged(Editable s) {
        }
    };

    private ActiveHashTag(int colorHashtag, int colorMention, OnHashTagClickListener listener, boolean isEnableHashtag, boolean isEnableMention, char... additionalHashTagCharacters) {
        this.colorHashtag = colorHashtag;
        this.colorMention = colorMention;
        this.isEnableHashtag = isEnableHashtag;
        this.isEnableMention = isEnableMention;
        onHashTagClickListener = listener;
        hashTagCharsList = new ArrayList<>();

        if (null != additionalHashTagCharacters) {
            for (char additionalChar : additionalHashTagCharacters) {
                hashTagCharsList.add(additionalChar);
            }
        }
    }

    public void operate(TextView textView) {
        operate(textView, true);
    }

    public void operate(TextView textView, boolean textWatcherEnable) {

        if (null == this.textView) {
            this.textView = textView;

            if (textWatcherEnable) {
                textView.addTextChangedListener(textWatcher);
            }

            textView.setText(textView.getText(), TextView.BufferType.SPANNABLE);

            if (null != onHashTagClickListener) {
                if (textWatcherEnable || null == textView.getMovementMethod()) {
                    textView.setMovementMethod(LinkMovementMethod.getInstance());
                }

            } else {
                // hash tags are not clickable
            }

            setColorsHashTags(textView.getText());
        } else {
            throw new RuntimeException("TextView is not null.");
        }
    }


    private void resetColorizeText(CharSequence text) {
        if (textView instanceof TagEditTextView) {
            Spannable spannable = ((Spannable) textView.getText());

            CharacterStyle[] spans = spannable.getSpans(0, text.length(), CharacterStyle.class);
            for (CharacterStyle span : spans) {
                spannable.removeSpan(span);
            }
        }

        setColorsHashTags(text);
    }

    private void setColorsHashTags(CharSequence text) {
        int startHash;

        int index = 0;
        while (index < text.length() - 1) {
            char sign = text.charAt(index);
            int nextNotLetter = index + 1;

            switch (sign) {
                case Constant.SHARP:
                    if (isEnableHashtag) {
                        startHash = index;
                        nextNotLetter = searchNextValidHashTagChar(text, startHash);

                        if (startHash == nextNotLetter) {
                            nextNotLetter++;
                        } else {
                            setColorEndHashTag(startHash, nextNotLetter, TYPE.HASHTAG);
                        }
                    }
                    break;
                case Constant.AT:
                    if (isEnableMention) {
                        startHash = index;
                        nextNotLetter = searchNextValidHashTagChar(text, startHash);

                        if (startHash == nextNotLetter) {
                            nextNotLetter++;
                        } else {
                            setColorEndHashTag(startHash, nextNotLetter, TYPE.MENTION);
                        }
                    }
                    break;
            }
            index = nextNotLetter;
        }
    }


    private int searchNextValidHashTagChar(CharSequence text, int start) {

        int nextNotLetter = -1; // skip '#"
        for (int index = start + 1; index < text.length(); index++) {
            char prevLetter = text.charAt(index - 1);
            char sign = text.charAt(index);

            if (Constant.SHARP == prevLetter) {
                if ((Constant.SHARP == sign) || (Constant.SPACE == sign)) {
                    nextNotLetter = start;
                    break;
                }
            }

            boolean isValidSign = Character.isLetterOrDigit(sign) || hashTagCharsList.contains(sign);
            if (!isValidSign) {
                nextNotLetter = index;
                break;
            }
        }
        if (-1 == nextNotLetter) {
            //end of text
            nextNotLetter = text.length();
        }
        return nextNotLetter;
    }


    private void setColorEndHashTag(int startIndex, int nextNotLetter, TYPE type) {
        try {
            Spannable s = (Spannable) textView.getText();

            CharacterStyle span;

            if (onHashTagClickListener != null) {
                span = new ClickableColorSpan(type == TYPE.HASHTAG ? colorHashtag : colorMention, this);
            } else {
                span = new ForegroundColorSpan(type == TYPE.HASHTAG ? colorHashtag : colorMention);
            }

            if (-1 < startIndex && -1 < nextNotLetter && startIndex < nextNotLetter) {
                s.setSpan(span, startIndex, nextNotLetter, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            }

        } catch (Exception e) {
            Log.e(TAG, " setColorEndHashTag e " + e.getMessage() + " startIndex:: " + startIndex + " nextNotLetter:: " + nextNotLetter);
        }
    }

    public List<String> getAllHashTags(boolean withHashes) {

        String text = textView.getText().toString();
        Spannable spannable = (Spannable) textView.getText();

        Set<String> hashTags = new LinkedHashSet<>();

        for (CharacterStyle span : spannable.getSpans(0, text.length(), CharacterStyle.class)) {
            hashTags.add(
                    text.substring(!withHashes ? spannable.getSpanStart(span) + 1/*skip "#" sign*/
                                    : spannable.getSpanStart(span),
                            spannable.getSpanEnd(span)));
        }

        if (null != hashTags) Log.d(TAG, " getAllHashTags hashTags result " + hashTags);

        return new ArrayList<>(hashTags);
    }

    public List<String> getAllHashTags() {
        return getAllHashTags(false);
    }

    @Override
    public void onHashTagClicked(String hashTag) {
        onHashTagClickListener.onHashTagClicked(hashTag);
    }
}
