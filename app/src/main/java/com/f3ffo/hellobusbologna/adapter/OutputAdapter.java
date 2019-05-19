package com.f3ffo.hellobusbologna.adapter;

import androidx.appcompat.widget.AppCompatTextView;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import androidx.annotation.NonNull;
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
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.card_output_layout, null);
        return new OutputViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OutputViewHolder holder, int position) {
        OutputCardViewItem outputCardViewItem = outputCardViewItemList.get(position);
        holder.buttonBusNumberOutput.setText(outputCardViewItem.getBusNumber());
        holder.textViewBusHourOutput.setText(outputCardViewItem.getBusHour());
        holder.textViewBusHourCompleteOutput.setText(outputCardViewItem.getBusHourComplete());
        holder.imageViewSatOrTable.setImageDrawable(context.getDrawable(outputCardViewItem.getImage()));
    }

    @Override
    public int getItemCount() {
        return outputCardViewItemList.size();
    }

    static class OutputViewHolder extends RecyclerView.ViewHolder {

        AppCompatTextView textViewBusHourOutput, textViewBusHourCompleteOutput;
        Button buttonBusNumberOutput;
        ImageView imageViewSatOrTable;

        OutputViewHolder(@NonNull View itemView) {
            super(itemView);
            buttonBusNumberOutput = itemView.findViewById(R.id.buttonBusNumberOutput);
            textViewBusHourOutput = itemView.findViewById(R.id.textViewBusHourOutput);
            textViewBusHourCompleteOutput = itemView.findViewById(R.id.textViewBusHourCompleteOutput);
            imageViewSatOrTable = itemView.findViewById(R.id.imageViewSatOrTable);
        }
    }

}