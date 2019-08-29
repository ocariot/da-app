package br.edu.uepb.nutes.ocariot.view.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import br.edu.uepb.nutes.ocariot.R;
import br.edu.uepb.nutes.ocariot.data.model.ocariot.ActivityType;
import br.edu.uepb.nutes.ocariot.data.model.ocariot.Child;
import br.edu.uepb.nutes.ocariot.data.model.ocariot.Child;
import br.edu.uepb.nutes.ocariot.utils.DateUtils;
import br.edu.uepb.nutes.ocariot.view.adapter.base.BaseAdapter;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * ChildListAdapter implementation.
 *
 * @author Copyright (c) 2018, NUTES/UEPB
 */
public class ChildListAdapter extends BaseAdapter<Child> {
    private final Context context;

    public ChildListAdapter(Context context) {
        this.context = context;
    }

    @Override
    public View createView(ViewGroup viewGroup, int viewType) {
        return View.inflate(context, R.layout.child_item, null);
    }

    @Override
    public RecyclerView.ViewHolder createViewHolder(View view) {
        return new ViewHolder(view);
    }

    @Override
    public void showData(RecyclerView.ViewHolder holder, int position, List<Child> itemsList) {
        if (holder instanceof ViewHolder) {
            final Child child = itemsList.get(position);
            ViewHolder h = (ViewHolder) holder;

            h.name.setText(child.getUsername());

            // OnClick Item
            h.mView.setOnClickListener(v -> {
                if (mListener != null) mListener.onItemClick(child);
            });
        }
    }

    @Override
    public void clearAnimation(RecyclerView.ViewHolder holder) {
        // Not implemented!
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        final View mView;

        @BindView(R.id.name_child)
        TextView name;

        ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);

            mView = view.getRootView();
        }
    }
}
