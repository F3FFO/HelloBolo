package com.f3ffo.hellobusbologna;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class OutputAdapter extends RecyclerView.Adapter<OutputAdapter.OutputViewHolder> {

    private Context context;
    private List<CardViewItem> cardViewItemList;

    public OutputAdapter(Context context, List<CardViewItem> cardViewItemList) {
        this.context = context;
        this.cardViewItemList = cardViewItemList;
    }

    @NonNull
    @Override
    public OutputViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.card_layout, null);
        return new OutputViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OutputViewHolder holder, int position) {
        CardViewItem cardViewItem = cardViewItemList.get(position);
        holder.buttonBusNumberOutput.setText(cardViewItem.getBusNumber());
        holder.textViewBusHourOutput.setText(cardViewItem.getBusHour());
        holder.textViewBusHourCompleteOutput.setText(cardViewItem.getBusHourComplete());
        holder.imageViewSatOrTable.setImageDrawable(context.getDrawable(cardViewItem.getImage()));
    }

    @Override
    public int getItemCount() {
        return cardViewItemList.size();
    }

    public static class OutputViewHolder extends RecyclerView.ViewHolder {

        TextView textViewBusHourOutput, textViewBusHourCompleteOutput;
        Button buttonBusNumberOutput;
        ImageView imageViewSatOrTable;

        public OutputViewHolder(@NonNull View itemView) {
            super(itemView);
            buttonBusNumberOutput = itemView.findViewById(R.id.buttonBusNumberOutput);
            textViewBusHourOutput = itemView.findViewById(R.id.textViewBusHourOutput);
            textViewBusHourCompleteOutput = itemView.findViewById(R.id.textViewBusHourCompleteOutput);
            imageViewSatOrTable = itemView.findViewById(R.id.imageViewSatOrTable);
        }
    }

}