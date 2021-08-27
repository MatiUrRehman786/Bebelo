package com.buzzware.bebelo.Addapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.buzzware.bebelo.Model.Child;
import com.buzzware.bebelo.Model.SectionHeader;
import com.buzzware.bebelo.R;
import com.buzzware.bebelo.ViewHolder.ChildViewHolder;
import com.buzzware.bebelo.ViewHolder.SectionViewHolder;
import com.intrusoft.sectionedrecyclerview.SectionRecyclerViewAdapter;
import java.util.List;

public class AdapterSectionRecycler extends SectionRecyclerViewAdapter<SectionHeader, Child, SectionViewHolder, ChildViewHolder> {

    Context context;

    public AdapterSectionRecycler(Context context, List<SectionHeader> sectionHeaderItemList) {
        super(context, sectionHeaderItemList);
        this.context = context;
    }

    @Override
    public SectionViewHolder onCreateSectionViewHolder(ViewGroup sectionViewGroup, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.header_item_lay, sectionViewGroup, false);
        return new SectionViewHolder(view);
    }

    @Override
    public ChildViewHolder onCreateChildViewHolder(ViewGroup childViewGroup, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.child_item_lay, childViewGroup, false);
        return new ChildViewHolder(view);
    }

    @Override
    public void onBindSectionViewHolder(SectionViewHolder sectionViewHolder, int sectionPosition, SectionHeader sectionHeader) {
        sectionViewHolder.name.setText(sectionHeader.getSectionText());
    }

    @Override
    public void onBindChildViewHolder(ChildViewHolder childViewHolder, int sectionPosition, int childPosition, Child child) {
        childViewHolder.name.setText(child.getName());
        childViewHolder.amount.setText(child.getAmount());
        childViewHolder.bottleIV.setImageResource(child.getImage());
    }
}
