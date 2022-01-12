package com.buzzware.bebelo.Addapter;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.buzzware.bebelo.Model.HourModel;
import com.buzzware.bebelo.R;
import com.buzzware.bebelo.databinding.TimeSchedualItemDesignBinding;
import com.buzzware.bebelo.interfaces.HoursCallback;

import java.util.List;


public class TimeScheduleForDialogAdapter extends RecyclerView.Adapter<TimeScheduleForDialogAdapter.ViewHolder>  {

    private List<HourModel> list;

    private Context mContext;


    public TimeScheduleForDialogAdapter(Context mContext, List<HourModel> list) {

        this.list = list;

        this.mContext = mContext;


    }

    @NonNull
    @Override
    public TimeScheduleForDialogAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        return new ViewHolder(TimeSchedualItemDesignBinding.inflate(LayoutInflater.from(parent.getContext()),
                parent, false));

    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, final int i) {

        HourModel hourModel=list.get(i);


        viewHolder.binding.dayNameTV.setText(hourModel.getDayName());

        if(hourModel.getStartTime().equals("0") || hourModel.getEndTime().equals("0")){

            viewHolder.binding.timeTV.setText("Closed");

        } else {

            viewHolder.binding.timeTV.setText(hourModel.getStartTime()+" - "+hourModel.getEndTime());

        }

    }



    @Override
    public int getItemCount() {

        return list.size();

    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        TimeSchedualItemDesignBinding binding;


        public ViewHolder(@NonNull TimeSchedualItemDesignBinding binding) {
            super(binding.getRoot());

            this.binding = binding;

        }

    }

}
