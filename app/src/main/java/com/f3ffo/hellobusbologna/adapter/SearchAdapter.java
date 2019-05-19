package com.f3ffo.hellobusbologna.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.RecyclerView;

import com.f3ffo.hellobusbologna.R;
import com.f3ffo.hellobusbologna.items.SearchListViewItem;

import java.util.ArrayList;
import java.util.List;

public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.SearchAdapterHolder> implements Filterable {

    private Context context;
    private List<SearchListViewItem> outputSearchViewItemList;
    private List<SearchListViewItem> outputSearchViewItemListFull;

    public SearchAdapter(Context context, List<SearchListViewItem> outputSearchViewItemList) {
        this.context = context;
        this.outputSearchViewItemList = outputSearchViewItemList;
        this.outputSearchViewItemListFull = new ArrayList<>(outputSearchViewItemList);
    }

    @NonNull
    @Override
    public SearchAdapterHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.search_list_item, null);
        return new SearchAdapter.SearchAdapterHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SearchAdapterHolder holder, int position) {
        SearchListViewItem searchListViewItem = outputSearchViewItemList.get(position);
        holder.textViewBusStopCodeOutput.setText(searchListViewItem.getBusStopCode());
        holder.textViewBusStopNameOutput.setText(searchListViewItem.getBusStopName());
        holder.textViewBusStopAddressOutput.setText(searchListViewItem.getBusStopAddres());
    }

    @Override
    public int getItemCount() {
        return outputSearchViewItemList.size();
    }

    @Override
    public Filter getFilter() {
        return searchFilter;
    }

    private Filter searchFilter = new Filter() {

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<SearchListViewItem> filteredItemList = new ArrayList<>();

            if (constraint == null || constraint.length() == 0) {
                filteredItemList.addAll(outputSearchViewItemListFull);
            } else {
                String filteredPattern = constraint.toString().toLowerCase().trim();

                for (SearchListViewItem item : outputSearchViewItemListFull) {
                    if (item.getBusStopCode().toLowerCase().contains(filteredPattern)) {
                        filteredItemList.add(item);
                    }
                }
            }
            FilterResults result = new FilterResults();
            result.values = filteredItemList;
            return result;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            outputSearchViewItemList.clear();
            outputSearchViewItemList.addAll((List) results.values);
            notifyDataSetChanged();
        }
    };

    class SearchAdapterHolder extends RecyclerView.ViewHolder {

        AppCompatTextView textViewBusStopCodeOutput, textViewBusStopNameOutput, textViewBusStopAddressOutput;

        SearchAdapterHolder(@NonNull View itemView) {
            super(itemView);
            textViewBusStopCodeOutput = itemView.findViewById(R.id.textViewBusStopCodeOutput);
            textViewBusStopNameOutput = itemView.findViewById(R.id.textViewBusStopNameOutput);
            textViewBusStopAddressOutput = itemView.findViewById(R.id.textViewBusStopAddressOutput);
        }
    }
}
