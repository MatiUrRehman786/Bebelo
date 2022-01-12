package com.buzzware.bebelo.Addapter;

import android.content.Context;
import android.content.res.Resources;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.buzzware.bebelo.Model.BottleModelForEdit;
import com.buzzware.bebelo.Model.HourModel;
import com.buzzware.bebelo.R;
import com.buzzware.bebelo.databinding.AddTimeItemDesignBinding;
import com.buzzware.bebelo.databinding.EditBarItemDesignBinding;
import com.buzzware.bebelo.interfaces.EditPricesCallback;
import com.buzzware.bebelo.interfaces.HoursCallback;
import com.buzzware.bebelo.retrofit.DetailModel.BarBottleItem;

import java.util.List;


public class NormalBottleAdapter extends RecyclerView.Adapter<NormalBottleAdapter.ViewHolder> {

    private List<BottleModelForEdit> list;

    private Context mContext;

    EditPricesCallback editPricesCallback;

    public NormalBottleAdapter(Context mContext, List<BottleModelForEdit> list, EditPricesCallback editPricesCallback) {

        this.list = list;

        this.mContext = mContext;

        this.editPricesCallback = editPricesCallback;

    }

    @NonNull
    @Override
    public NormalBottleAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        return new ViewHolder(EditBarItemDesignBinding.inflate(LayoutInflater.from(parent.getContext()),
                parent, false));

    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, final int i) {

        BottleModelForEdit bottleModelForEdit = list.get(i);

        viewHolder.binding.nameTV.setText(bottleModelForEdit.getName());

        viewHolder.binding.amountET.setText(bottleModelForEdit.getPrice());


        try {

            Resources resources = mContext.getResources();
            final int resourceId = resources.getIdentifier(bottleModelForEdit.getImage(), "drawable",
                    mContext.getPackageName());

            viewHolder.binding.bottleIV.setImageResource(resourceId);

        } catch (Exception e) {
            Log.d("image", "image not found in drawable" + e.getLocalizedMessage());
        }


        viewHolder.binding.amountET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {



            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (viewHolder.binding.amountET.getText().toString().length() > 0) {

                    String value = viewHolder.binding.amountET.getText().toString();

                    editPricesCallback.onPriceChange("normal", i, value);

                }
            }
        });

    }


    @Override
    public int getItemCount() {

        return list.size();

    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        EditBarItemDesignBinding binding;


        public ViewHolder(@NonNull EditBarItemDesignBinding binding) {
            super(binding.getRoot());

            this.binding = binding;

        }

    }

}
