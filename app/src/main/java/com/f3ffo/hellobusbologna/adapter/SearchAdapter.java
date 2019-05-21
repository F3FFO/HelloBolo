package com.f3ffo.hellobusbologna.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.RecyclerView;

import com.f3ffo.hellobusbologna.R;
import com.f3ffo.hellobusbologna.items.SearchListViewItem;

import java.util.ArrayList;
import java.util.List;

public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.SearchAdapterHolder> implements Filterable {

    private List<SearchListViewItem> outputSearchViewItemList;
    private List<SearchListViewItem> outputSearchViewItemListFull;
    private OnItemClickListener mListener;
    private Context context;

    public SearchAdapter(Context context, List<SearchListViewItem> outputSearchViewItemList) {
        this.context = context;
        this.outputSearchViewItemList = outputSearchViewItemList;
        this.outputSearchViewItemListFull = new ArrayList<>(outputSearchViewItemList);
    }

    @NonNull
    @Override
    public SearchAdapterHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.search_list_item, parent, false);
        return new SearchAdapter.SearchAdapterHolder(view, mListener);
    }

    @Override
    public void onBindViewHolder(@NonNull SearchAdapterHolder holder, int position) {
        SearchListViewItem searchListViewItem = outputSearchViewItemList.get(position);
        holder.imageViewSearch.setImageDrawable(context.getDrawable(R.drawable.ic_search));
        holder.textViewBusStopCodeSearch.setText(searchListViewItem.getBusStopCode());
        holder.textViewBusStopNameSearch.setText(searchListViewItem.getBusStopName());
        holder.textViewBusStopAddressSearch.setText(searchListViewItem.getBusStopAddres());
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
                for (SearchListViewItem item : outputSearchViewItemListFull) {
                    if (item.getBusStopCode().toUpperCase().contains(constraint.toString().toUpperCase()) ||
                            item.getBusStopName().toUpperCase().contains(constraint.toString().toUpperCase()) ||
                            item.getBusStopAddres().toUpperCase().contains(constraint.toString().toUpperCase())) {
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

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;
    }

    static class SearchAdapterHolder extends RecyclerView.ViewHolder {

        AppCompatTextView textViewBusStopCodeSearch, textViewBusStopNameSearch, textViewBusStopAddressSearch;
        AppCompatImageView imageViewSearch;

        SearchAdapterHolder(@NonNull View itemView, final OnItemClickListener listener) {
            super(itemView);
            imageViewSearch = itemView.findViewById(R.id.imageViewSearch);
            textViewBusStopCodeSearch = itemView.findViewById(R.id.textViewBusStopCodeSearch);
            textViewBusStopNameSearch = itemView.findViewById(R.id.textViewBusStopNameSearch);
            textViewBusStopAddressSearch = itemView.findViewById(R.id.textViewBusStopAddressSearch);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            listener.onItemClick(position);
                        }
                    }
                }
            });
        }
    }
}
