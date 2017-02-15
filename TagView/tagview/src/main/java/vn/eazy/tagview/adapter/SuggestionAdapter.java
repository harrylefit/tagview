package vn.eazy.tagview.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import vn.eazy.tagview.listener.DataItemListener;
import vn.eazy.tagview.model.BaseData;

/**
 * Created by Harry on 2/15/17.
 */

public abstract class SuggestionAdapter<T extends BaseData, V extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<V> implements View.OnClickListener {
    private Context context;
    private List<T> list;
    private DataItemListener onItemClickListener;
    private CallbackDataListener dataListener;

    public interface CallbackDataListener {
        void callbackData(BaseData data);
    }

    public SuggestionAdapter(Context context) {
        this.context = context;
        list = new ArrayList<>();
    }

    protected abstract V getLayoutViewHolder(ViewGroup parent, int viewType);

    @Override
    public void onBindViewHolder(V holder, int position) {
        onSuggestBindViewHolder(holder, position, list.get(position));
        if (onItemClickListener != null) {
            holder.itemView.setTag(list.get(position));
            holder.itemView.setOnClickListener(this);
        }
    }

    protected abstract void onSuggestBindViewHolder(V holder, int pos, T data);

    @Override
    public int getItemCount() {
        return list.size();
    }

    public void addAll(List<T> data) {
        int pos = getItemCount();
        list.addAll(data);
        notifyItemRangeChanged(pos, list.size());
    }

    public void add(T data) {
        list.add(data);
        notifyItemInserted(list.size());
    }

    public void clearAndAddAll(List<T> data) {
        list.clear();
        addAll(data);
        notifyDataSetChanged();
    }

    public void clear() {
        list.clear();
        notifyDataSetChanged();
    }

    public Context getContext() {
        return context;
    }

    @Override
    public V onCreateViewHolder(ViewGroup parent, int viewType) {
        return getLayoutViewHolder(parent, viewType);
    }

    public void setOnItemClickListener(DataItemListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    @Override
    public void onClick(View view) {
        onItemClickListener.onClickItem(view, (BaseData) view.getTag());
        if (dataListener != null) {
            dataListener.callbackData((BaseData) view.getTag());
        }
    }

    public void setDataListener(CallbackDataListener dataListener) {
        this.dataListener = dataListener;
    }
}
