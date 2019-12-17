package com.f3ffo.busbolo.output;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.recyclerview.widget.RecyclerView;

import com.f3ffo.busbolo.R;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textview.MaterialTextView;

import java.util.List;

public class OutputAdapter extends RecyclerView.Adapter<OutputAdapter.OutputViewHolder> {

    private Context context;
    private List<OutputItem> outputItemList;

    public OutputAdapter(Context context, List<OutputItem> outputItemList) {
        this.context = context;
        this.outputItemList = outputItemList;
    }

    @NonNull
    @Override
    public OutputViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.output_layout, parent, false);
        return new OutputViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OutputViewHolder holder, int position) {
        OutputItem outputItem = outputItemList.get(position);
        holder.buttonBusNumberOutput.setText(outputItem.getBusNumber());
        holder.textViewBusHourOutput.setText(outputItem.getBusHour());
        holder.textViewBusHourCompleteOutput.setText(outputItem.getBusHourComplete());
        holder.imageViewSatOrTable.setImageDrawable(context.getDrawable(outputItem.getSatelliteOrHour()));
        int isHandicap = outputItem.getHandicap();
        if (isHandicap != 0) {
            holder.imageViewHandicap.setImageDrawable(context.getDrawable(isHandicap));
        }
    }

    @Override
    public int getItemCount() {
        return outputItemList.size();
    }

    static class OutputViewHolder extends RecyclerView.ViewHolder {

        MaterialTextView textViewBusHourOutput, textViewBusHourCompleteOutput;
        MaterialButton buttonBusNumberOutput;
        AppCompatImageView imageViewSatOrTable, imageViewHandicap;

        OutputViewHolder(@NonNull View itemView) {
            super(itemView);
            buttonBusNumberOutput = itemView.findViewById(R.id.buttonBusNumberOutput);
            textViewBusHourOutput = itemView.findViewById(R.id.textViewBusHourOutput);
            textViewBusHourCompleteOutput = itemView.findViewById(R.id.textViewBusHourCompleteOutput);
            imageViewSatOrTable = itemView.findViewById(R.id.imageViewSatOrTable);
            imageViewHandicap = itemView.findViewById(R.id.imageViewHandicap);
        }
    }
}