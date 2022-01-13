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
import com.buzzware.bebelo.databinding.AddTimeItemDesignBinding;
import com.buzzware.bebelo.interfaces.HoursCallback;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;


public class HoursAdapter extends RecyclerView.Adapter<HoursAdapter.ViewHolder>  {

    private List<HourModel> list;

    private Context mContext;

    HoursCallback hoursCallback;

    public HoursAdapter(Context mContext, List<HourModel> list,HoursCallback hoursCallback) {

        this.list = list;

        this.mContext = mContext;

        this.hoursCallback = hoursCallback;

    }

    @NonNull
    @Override
    public HoursAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        return new ViewHolder(AddTimeItemDesignBinding.inflate(LayoutInflater.from(parent.getContext()),
                parent, false));

    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, final int i) {

        HourModel hourModel=list.get(i);

        if(hourModel.isChildOfDay()){

            viewHolder.binding.button.setImageResource(R.drawable.remove_icon);

            viewHolder.binding.dayNameTV.setText("");

        } else {

            viewHolder.binding.dayNameTV.setText(hourModel.getDayName());

        }

        if(hourModel.getEndTime().equals("0"))
            viewHolder.binding.endTimeET.setText("");

        else
            viewHolder.binding.endTimeET.setText(hourModel.getEndTime());

        if(hourModel.getStartTime().equals("0"))
            viewHolder.binding.startTimeET.setText("");

        else
            viewHolder.binding.startTimeET.setText(hourModel.getStartTime());


        viewHolder.binding.button.setOnClickListener(v->{

            if(hourModel.isChildOfDay()){

                hoursCallback.onItemClick(viewHolder.binding.dayNameTV.getText().toString(),i,"remove");

            } else {

                hoursCallback.onItemClick(viewHolder.binding.dayNameTV.getText().toString(),i,"add");

            }

        });
        viewHolder.binding.startTimeET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {

                if(viewHolder.binding.startTimeET.getText().toString().length()==2){

                    int value=Integer.parseInt(viewHolder.binding.startTimeET.getText().toString());

                    if(value<24){

                        hoursCallback.onDataChange(value+":00",i,true );

                        viewHolder.binding.startTimeET.setText(value+":00");

                    } else {

                        viewHolder.binding.startTimeET.setText("");

                    }

                } else if (viewHolder.binding.startTimeET.getText().toString().length()==3){

                    viewHolder.binding.startTimeET.setText("");

                }

            }
        });
        viewHolder.binding.endTimeET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {

                if(viewHolder.binding.endTimeET.getText().toString().length()==2){

                    if(!viewHolder.binding.startTimeET.getText().toString().equals("")){

                        int sValue=0;

                        if(viewHolder.binding.startTimeET.getText().toString().contains(":")){

                            String[] data=viewHolder.binding.startTimeET.getText().toString().split(":");

                            sValue=Integer.parseInt(data[0]);

                        } else {

                            sValue=Integer.parseInt(viewHolder.binding.startTimeET.getText().toString());

                        }

                        int eValue=Integer.parseInt(viewHolder.binding.endTimeET.getText().toString());

                        if(eValue>sValue){

                            if(eValue<=24){

                                hoursCallback.onDataChange(eValue+":00",i,false );

                                viewHolder.binding.endTimeET.setText(eValue+":00");

                            } else {

                                viewHolder.binding.endTimeET.setText("");

                            }
                        } else {

                            viewHolder.binding.endTimeET.setText("");

                        }
                    }

                } else if (viewHolder.binding.endTimeET.getText().toString().length()==3){

                    viewHolder.binding.endTimeET.setText("");

                }

            }
        });


    }



    @Override
    public int getItemCount() {

        return list.size();

    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        AddTimeItemDesignBinding binding;


        public ViewHolder(@NonNull AddTimeItemDesignBinding binding) {
            super(binding.getRoot());

            this.binding = binding;

        }

    }

}
