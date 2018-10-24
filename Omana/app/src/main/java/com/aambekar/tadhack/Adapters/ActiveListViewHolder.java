package com.aambekar.tadhack.Adapters;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.aambekar.tadhack.R;

public class ActiveListViewHolder  extends RecyclerView.ViewHolder {

    public TextView name;
    public TextView number;
    public ImageView btn_delete;


    ActiveListViewHolder(View view) {
        super(view);
        name = view.findViewById(R.id.tv_name);
        number = view.findViewById(R.id.tv_number);
        btn_delete = view.findViewById(R.id.btn_remove);
    }

}
