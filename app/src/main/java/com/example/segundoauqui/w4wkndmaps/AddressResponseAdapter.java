package com.example.segundoauqui.w4wkndmaps;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.segundoauqui.w4wkndmaps.model.Result;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by segundoauqui on 8/28/17.
 */

public class AddressResponseAdapter extends RecyclerView.Adapter<AddressResponseAdapter.ViewHolder> {

    private static final String TAG = "Adapter";
    List<Result>  placesList = new ArrayList<>();
    Context context;
    private int positionGlobal = -1;

    public AddressResponseAdapter(List<Result> placesList){
        this.placesList = placesList;
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        ImageView ivImage;
        TextView tvText1, tvText2,tvText3,tvText4;
        public ViewHolder(View itemView) {
            super(itemView);

            ivImage = (ImageView) itemView.findViewById(R.id.ivImage);
            tvText1= (TextView) itemView.findViewById(R.id.tvText1);
            tvText2 = (TextView) itemView.findViewById(R.id.tvText2);
            tvText3 = (TextView) itemView.findViewById(R.id.tvText3);
            tvText4 = (TextView) itemView.findViewById(R.id.tvText4);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        context = parent.getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.addressresponse, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final Result itemm = placesList.get(position);
        holder.tvText1.setText(itemm.getName());
        holder.tvText2.setText(itemm.getVicinity());
        holder.tvText3.setText(itemm.getTypes().get(0).toString());
        holder.tvText4.setText(String.valueOf(itemm.getRating()));
        Glide.with(holder.itemView.getContext()).load(itemm.getIcon()).into(holder.ivImage);
    }
    private void setAnimation(View viewToAnimate, int position)
    {
        // If the bound view wasn't previously displayed on screen, it's animated
        if (position > positionGlobal) {
            ScaleAnimation anim = new ScaleAnimation(0.0f, 1.0f, 0.0f, 1.0f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
            anim.setDuration(new Random().nextInt(501));//to make duration random number between [0,501)
            viewToAnimate.startAnimation(anim);
            positionGlobal = position;
        }
    }


    @Override
    public int getItemCount() {
        return placesList.size();
    }

}
