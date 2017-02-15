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

import vn.eazy.tagview.R;
import vn.eazy.tagview.adapter.SuggestionAdapter;
import vn.eazy.tagview.listener.DataItemListener;
import vn.eazy.tagview.model.BaseData;


/**
 * Created by Harry on 2/14/17.
 */

public class PopupSuggestionWindow extends RelativePopupWindow implements SuggestionAdapter.CallbackDataListener {
    private RecyclerView rvData;
    private SuggestionAdapter adapter;
    private LinearLayoutManager llm;
    private DataItemListener onItemClickListener;

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
    }

    public void setAdapter(SuggestionAdapter adapter) {
        this.adapter = adapter;
        this.adapter.setDataListener(this);
        if (rvData != null) {
            rvData.setAdapter(adapter);
        }
    }

    public SuggestionAdapter getAdapter() {
        return adapter;
    }

    public void setOnItemClickListener(DataItemListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
        adapter.setOnItemClickListener(this.onItemClickListener);
    }

    @Override
    public void callbackData(BaseData data) {
        dismiss();
    }
}
