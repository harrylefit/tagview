package vn.eazy.tagview.widget;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.labo.kaji.relativepopupwindow.RelativePopupWindow;

import vn.eazy.tagview.R;

/**
 * Created by Harry on 2/14/17.
 */

public class PopupSuggestionWindow extends RelativePopupWindow {
    public PopupSuggestionWindow(Context context) {
        super(context);
        setContentView(LayoutInflater.from(context).inflate(R.layout.listview_popup, null));
        setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        setFocusable(false);
        setOutsideTouchable(true);
        setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        // Disable default animation for circular reveal
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            setAnimationStyle(0);
        }

    }
}
