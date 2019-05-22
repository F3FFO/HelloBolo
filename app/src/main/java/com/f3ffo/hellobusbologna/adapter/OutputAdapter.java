package com.f3ffo.hellobusbologna.adapter;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.recyclerview.widget.RecyclerView;

import com.f3ffo.hellobusbologna.items.OutputCardViewItem;
import com.f3ffo.hellobusbologna.R;

import java.util.List;

public class OutputAdapter extends RecyclerView.Adapter<OutputAdapter.OutputViewHolder> {

    private Context context;
    private List<OutputCardViewItem> outputCardViewItemList;

    public OutputAdapter(Context context, List<OutputCardViewItem> outputCardViewItemList) {
        this.context = context;
        this.outputCardViewItemList = outputCardViewItemList;
    }

    @NonNull
    @Override
    public OutputViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.card_output_layout, parent, false);
        return new OutputViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OutputViewHolder holder, int position) {
        OutputCardViewItem outputCardViewItem = outputCardViewItemList.get(position);
        holder.buttonBusNumberOutput.setText(outputCardViewItem.getBusNumber());
        holder.textViewBusHourOutput.setText(outputCardViewItem.getBusHour());
        holder.textViewBusHourCompleteOutput.setText(outputCardViewItem.getBusHourComplete());
        holder.imageViewSatOrTable.setImageDrawable(context.getDrawable(outputCardViewItem.getSatelliteOrHour()));
        int isHandicap = outputCardViewItem.getHandicap();
        if (isHandicap != 0) {
            holder.imageViewHandicap.setImageDrawable(context.getDrawable(isHandicap));
        }

    }

    @Override
    public int getItemCount() {
        return outputCardViewItemList.size();
    }

    static class OutputViewHolder extends RecyclerView.ViewHolder {

        AppCompatTextView textViewBusHourOutput, textViewBusHourCompleteOutput;
        Button buttonBusNumberOutput;
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