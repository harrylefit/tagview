package vn.eazy.tagview.core;

import android.support.annotation.NonNull;
import android.text.Editable;
import android.text.Spannable;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.text.style.CharacterStyle;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import vn.eazy.tagview.event.ActionEvent;
import vn.eazy.tagview.event.SuggestionItemEvent;
import vn.eazy.tagview.widget.TagEditTextView;

public final class ActiveHashTag implements ClickableColorSpan.OnHashTagClickListener, ActionEvent {
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

    private String lastHashTag = "";
    private String lastMention = "";
    private String lastContent = "";

    private boolean isChooseDataFromSuggestionList = false;

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

    @Override
    public void register() {
        if (textView instanceof TagEditTextView) {
            if (!EventBus.getDefault().isRegistered(this)) {
                EventBus.getDefault().register(this);
            }
        }
    }

    @Override
    public void unregister() {
        if (textView instanceof TagEditTextView) {
            EventBus.getDefault().unregister(this);
        }
    }

    @Override
    public void release() {
        unregister();
    }


    private final TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence text, int start, int before, int count) {
            if (text.length() > 0) {
                resetColorizeText(text);

                if (textView instanceof TagEditTextView) {
                    int sizeText = text.length() - 1;
                    switch (text.charAt(sizeText)) {
                        case Constant.SPACE:
                        case Constant.AT:
                        case Constant.SHARP:
                            ((TagEditTextView) textView).dimissPopup();
                            break;
                    }
                }

            }
        }

        @Override
        public synchronized void afterTextChanged(Editable s) {
        }
    };

    public void setOnHashTagClickListener(OnHashTagClickListener onHashTagClickListener) {
        this.onHashTagClickListener = onHashTagClickListener;
        setHashTagLisenter(this.onHashTagClickListener);
    }

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

    @Subscribe
    public void onEvent(SuggestionItemEvent suggestionItemEvent) {
        if(textView instanceof TagEditTextView) {
            isChooseDataFromSuggestionList = true;
            int indexLastMention = textView.getText().toString().lastIndexOf(getLastMention(textView.getText()));
            int indexLastHashTag = textView.getText().toString().lastIndexOf(getLastHashTag(textView.getText()));
            if (indexLastMention == textView.length() || indexLastHashTag > indexLastMention) {
                textView.setText(textView.getText().toString().replace("#" + getLastHashTag(textView.getText())
                        , "#" + suggestionItemEvent.getData().getTag()));

            } else {
                textView.setText(textView.getText().toString().replace("@" + getLastMention(textView.getText())
                        , "@" + suggestionItemEvent.getData().getTag()));
            }
        }
        textView.requestFocus();
        ((TagEditTextView)textView).setSelection(textView.getText().length());
    }

    public void operate(TextView textView) {
        operate(textView, true);
    }

    public void removeTextWatcher() {
        textView.removeTextChangedListener(textWatcher);
    }

    public void operate(TextView textView, boolean textWatcherEnable) {
        if (null == this.textView) {
            this.textView = textView;

            register();

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


    public void setHashTagLisenter(OnHashTagClickListener hashTagLisenter) {
        if (null != hashTagLisenter) {
            if (null == textView.getMovementMethod()) {
                textView.setMovementMethod(LinkMovementMethod.getInstance());
            }
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

    public String getLastHashTag(@NonNull CharSequence text) {
        int indexOfLastHashTag = text.toString().lastIndexOf(Constant.SHARP);
        if (indexOfLastHashTag == -1) {
            return "";
        } else {
            String[] contents = text.toString().substring(indexOfLastHashTag + 1, text.length()).toString().split(" ");
            if (contents.length != 0) {
                return contents[0].trim();
            }
            return "";
        }
    }


    public String getLastMention(@NonNull CharSequence text) {
        int indexOfLastMention = text.toString().lastIndexOf(Constant.AT);
        if (indexOfLastMention == -1) {
            return "";
        } else {
            String[] contents = text.toString().substring(indexOfLastMention + 1, text.length()).toString().split(" ");
            if (contents.length != 0) {
                return contents[0].trim();
            }
            return "";
        }
    }

    private boolean isContainSpaceBehindLastLinkable(String text, String linkable) {
        int indexOf = text.lastIndexOf(linkable);
        return text.substring(indexOf, text.length()).contains(" ");
    }

    private void setColorsHashTags(CharSequence text) {
        int startHash;

        int index = 0;
        boolean isRunListenter = false;
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

            callbackContent(type);

            if (-1 < startIndex && -1 < nextNotLetter && startIndex < nextNotLetter) {
                s.setSpan(span, startIndex, nextNotLetter, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            }

        } catch (Exception e) {
            Log.e(TAG, " setColorEndHashTag e " + e.getMessage() + " startIndex:: " + startIndex + " nextNotLetter:: " + nextNotLetter);
        }
    }

    private void callbackContent(TYPE type) {
            if (type == TYPE.HASHTAG) {
                if (textView instanceof TagEditTextView && ((TagEditTextView) textView).getOnTypingListener() != null
                        && !isContainSpaceBehindLastLinkable(textView.getText().toString(), getLastHashTag(textView.getText()))
                        && !lastHashTag.equals(getLastHashTag(textView.getText()))) {
                    lastHashTag = getLastHashTag(textView.getText());
                    if(!isChooseDataFromSuggestionList) {
                        ((TagEditTextView) textView).getOnTypingListener().onTypingHashTag(getLastHashTag(textView.getText()));
                    }else{
                        isChooseDataFromSuggestionList = false;
                }
            } else {
                if (textView instanceof TagEditTextView && ((TagEditTextView) textView).getOnTypingListener() != null
                        && !isContainSpaceBehindLastLinkable(textView.getText().toString(), getLastMention(textView.getText()))
                        && !lastMention.equals(getLastMention(textView.getText()))) {
                    lastMention = getLastMention(textView.getText());
                    if(!isChooseDataFromSuggestionList) {
                        ((TagEditTextView) textView).getOnTypingListener().onTypingMention(getLastMention(textView.getText()));
                    }else{
                        isChooseDataFromSuggestionList = false;
                    }
                }
            }
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
