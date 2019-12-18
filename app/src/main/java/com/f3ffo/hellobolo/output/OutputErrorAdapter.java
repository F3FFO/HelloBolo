package com.f3ffo.hellobolo.output;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.recyclerview.widget.RecyclerView;

import com.f3ffo.hellobolo.R;
import com.google.android.material.textview.MaterialTextView;

import java.util.List;

public class OutputErrorAdapter extends RecyclerView.Adapter<OutputErrorAdapter.OutputErrorViewHolder> {

    private Context context;
    private List<OutputItem> outputItemList;

    public OutputErrorAdapter(Context context, List<OutputItem> outputItemList) {
        this.context = context;
        this.outputItemList = outputItemList;
    }

    @NonNull
    @Override
    public OutputErrorViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.output_error_layout, parent, false);
        return new OutputErrorViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OutputErrorViewHolder holder, int position) {
        OutputItem outputItem = outputItemList.get(position);
        holder.textViewErrorOutput.setText(outputItem.getError());
        holder.imageViewErrorOutput.setImageDrawable(context.getDrawable(R.drawable.output_error));
    }

    @Override
    public int getItemCount() {
        return outputItemList.size();
    }

    static class OutputErrorViewHolder extends RecyclerView.ViewHolder {

        MaterialTextView textViewErrorOutput;
        AppCompatImageView imageViewErrorOutput;

        OutputErrorViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewErrorOutput = itemView.findViewById(R.id.textViewErrorOutput);
            imageViewErrorOutput = itemView.findViewById(R.id.imageViewErrorOutput);
        }
    }
}