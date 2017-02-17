package vn.eazy.tagview.widget;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.labo.kaji.relativepopupwindow.RelativePopupWindow;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import vn.eazy.tagview.R;
import vn.eazy.tagview.adapter.SuggestionAdapter;
import vn.eazy.tagview.event.SuggestionItemEvent;


/**
 * Created by Harry on 2/14/17.
 */

public class PopupSuggestionWindow extends RelativePopupWindow {
    private RecyclerView rvData;
    private SuggestionAdapter adapter;
    private LinearLayoutManager llm;

    public PopupSuggestionWindow(Context context, int widthPopup) {
        super(context);
        setContentView(LayoutInflater.from(context).inflate(R.layout.listview_popup, null));
        setWidth(widthPopup);
        setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        setFocusable(false);
        setOutsideTouchable(true);
        setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        // Disable default animation for circular reveal
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            setAnimationStyle(0);
        }

        rvData = (RecyclerView) getContentView().findViewById(R.id.rvData);
        llm = new LinearLayoutManager(context);
        rvData.setLayoutManager(llm);

        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
    }

    @Subscribe
    public void onEvent(SuggestionItemEvent suggestionItemEvent){
        dismiss();
    }

    public void release(){
        EventBus.getDefault().unregister(this);
    }

    public void setAdapter(SuggestionAdapter adapter) {
        this.adapter = adapter;
        if (rvData != null) {
            rvData.setAdapter(adapter);
        }
    }

    public SuggestionAdapter getAdapter() {
        return adapter;
    }
}
