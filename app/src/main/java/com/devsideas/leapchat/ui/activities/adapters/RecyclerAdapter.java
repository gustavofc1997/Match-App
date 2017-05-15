package com.devsideas.leapchat.ui.activities.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.devsideas.leapchat.R;
import com.devsideas.leapchat.domain.NearDevice;
import com.devsideas.leapchat.util.ItemClick;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Latitude on 06/04/2017.
 */

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ViewHolder> {

    private Context ctx;
    private ArrayList<NearDevice> mList;
    ItemClick listener;

    public RecyclerAdapter(Context context,ItemClick itemClick) {
        this.ctx = context;
        mList = new ArrayList<>();
        listener=itemClick;
    }
    public NearDevice getitem(int pos){
        return  mList.get(pos);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item, parent, false);

        final ViewHolder mViewHolder = new ViewHolder(view);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onItemClick(v, mViewHolder.getPosition());

            }
        });
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.txt_name.setText(mList.get(position).getmName());
        holder.txt_token.setText(mList.get(position).getmToken());
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public void setItems(NearDevice device) {
        mList.add(device);
        notifyDataSetChanged();
    }

    public void rmItem(NearDevice device) {
        mList.remove(device);
        notifyDataSetChanged();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.txt_name)
        TextView txt_name;
        @BindView(R.id.txt_token)
        TextView txt_token;

        ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
