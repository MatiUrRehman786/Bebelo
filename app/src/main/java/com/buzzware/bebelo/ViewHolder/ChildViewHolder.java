package com.buzzware.bebelo.ViewHolder;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;

import com.buzzware.bebelo.R;

public class ChildViewHolder extends RecyclerView.ViewHolder {

    public TextView name, amount;
    public ImageView bottleIV;

    public ChildViewHolder(View itemView) {
        super(itemView);
        name = itemView.findViewById(R.id.nameTV);
        amount = itemView.findViewById(R.id.amountTV);
        bottleIV = itemView.findViewById(R.id.bottleIV);
    }
}
