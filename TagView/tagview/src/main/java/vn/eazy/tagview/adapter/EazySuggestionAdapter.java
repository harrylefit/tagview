package vn.eazy.tagview.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import vn.eazy.tagview.R;
import vn.eazy.tagview.model.SimpleData;

/**
 * Created by Harry on 2/15/17.
 */

public class EazySuggestionAdapter extends SuggestionAdapter<SimpleData, EazySuggestionAdapter.ViewHolder> {

    public EazySuggestionAdapter(Context context,int heightOfItem) {
        super(context,heightOfItem);
    }

    @Override
    protected ViewHolder getLayoutViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(getContext()).inflate(R.layout.item_suggestion, parent,false));
    }

    @Override
    protected void onSuggestBindViewHolder(ViewHolder holder, int pos, SimpleData data) {
        holder.tvTitle.setText(data.getItemTitle());

    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView tvTitle;

        public ViewHolder(View itemView) {
            super(itemView);
            tvTitle = (TextView) itemView.findViewById(R.id.tvTitle);
        }
    }
}
