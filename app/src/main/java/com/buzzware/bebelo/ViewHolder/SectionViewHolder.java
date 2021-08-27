package com.buzzware.bebelo.ViewHolder;

import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.buzzware.bebelo.R;

public class SectionViewHolder extends RecyclerView.ViewHolder {
    public TextView name;
    public SectionViewHolder(View itemView) {
        super(itemView);
         name = itemView.findViewById(R.id.nameTV);
    }
}
