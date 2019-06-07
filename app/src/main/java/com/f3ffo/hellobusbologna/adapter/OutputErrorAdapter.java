package com.f3ffo.hellobusbologna.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.RecyclerView;

import com.f3ffo.hellobusbologna.R;
import com.f3ffo.hellobusbologna.model.OutputCardViewItem;

import java.util.List;

public class OutputErrorAdapter extends RecyclerView.Adapter<OutputErrorAdapter.OutputErrorViewHolder> {

    private Context context;
    private List<OutputCardViewItem> outputCardViewItemList;

    public OutputErrorAdapter(Context context, List<OutputCardViewItem> outputCardViewItemList) {
        this.context = context;
        this.outputCardViewItemList = outputCardViewItemList;
    }

    @NonNull
    @Override
    public OutputErrorViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.output_error_layout, parent, false);
        return new OutputErrorViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OutputErrorViewHolder holder, int position) {
        OutputCardViewItem outputCardViewItem = outputCardViewItemList.get(position);
        holder.textViewErrorOutput.setText(outputCardViewItem.getError());
        holder.imageViewErrorOutput.setImageDrawable(context.getDrawable(outputCardViewItem.getErrorImage()));
    }

    @Override
    public int getItemCount() {
        return outputCardViewItemList.size();
    }

    static class OutputErrorViewHolder extends RecyclerView.ViewHolder {

        AppCompatTextView textViewErrorOutput;
        AppCompatImageView imageViewErrorOutput;

        OutputErrorViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewErrorOutput = itemView.findViewById(R.id.textViewErrorOutput);
            imageViewErrorOutput = itemView.findViewById(R.id.imageViewErrorOutput);
        }
    }
}
