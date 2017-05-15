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
import com.devsideas.leapchat.util.RecyclerListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Latitude on 06/04/2017.
 */

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ViewHolder> {

    private Context ctx;
    private RecyclerListener.OnItemClickListener mItemClickListener;

    private ArrayList<NearDevice> mList;
    ItemClick listener;

    public RecyclerAdapter(Context context) {
        this.ctx = context;
        mList = new ArrayList<>();
    }

    public void setItemClickListener(RecyclerListener.OnItemClickListener mItemClickListener) {
        this.mItemClickListener = mItemClickListener;
    }

    public NearDevice getitem(int pos) {
        return mList.get(pos);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.txt_name.setText(mList.get(position).getmName());
        Picasso.with(ctx).load(mList.get(position).getmPicture()).into(holder.circleImageView);
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public void setItems(ArrayList<NearDevice> devices) {
        this.mList = devices;
        notifyDataSetChanged();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.txt_name)
        TextView txt_name;
        @BindView(R.id.img_photo)
        CircleImageView circleImageView;

        ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (mItemClickListener != null) {
                        mItemClickListener.onItemClick(view, getPosition());

                    }
                }
            });
        }
    }
}
