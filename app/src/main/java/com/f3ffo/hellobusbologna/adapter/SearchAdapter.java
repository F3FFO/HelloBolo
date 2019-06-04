package com.f3ffo.hellobusbologna.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.RecyclerView;

import com.f3ffo.hellobusbologna.R;
import com.f3ffo.hellobusbologna.model.SearchListViewItem;

import java.util.ArrayList;
import java.util.List;

public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.SearchAdapterHolder> implements Filterable {

    private List<SearchListViewItem> outputSearchViewItemList;
    private List<SearchListViewItem> outputSearchViewItemListFull;
    private OnItemClickListener itemClickListener;
    private OnFavouriteButtonClickListener favouriteButtonClickListener;

    public SearchAdapter(List<SearchListViewItem> outputSearchViewItemList) {
        this.outputSearchViewItemList = outputSearchViewItemList;
        this.outputSearchViewItemListFull = new ArrayList<>(outputSearchViewItemList);
    }

    @NonNull
    @Override
    public SearchAdapterHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.search_list_item, parent, false);
        return new SearchAdapterHolder(view, itemClickListener, favouriteButtonClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull SearchAdapterHolder holder, int position) {
        SearchListViewItem searchListViewItem = outputSearchViewItemList.get(position);
        holder.textViewBusStopCodeSearch.setText(searchListViewItem.getBusStopCode());
        holder.textViewBusStopNameSearch.setText(searchListViewItem.getBusStopName());
        holder.textViewBusStopAddressSearch.setText(searchListViewItem.getBusStopAddress());
        holder.imageButtonFavouriteSearch.setImageResource(R.drawable.round_favourite_border);
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
                            item.getBusStopAddress().toUpperCase().contains(constraint.toString().toUpperCase())) {
                        filteredItemList.add(item);
                    }
                }
            }
            FilterResults result = new FilterResults();
            result.values = filteredItemList;
            return result;
        }

        @SuppressWarnings("unchecked")
        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            outputSearchViewItemList.clear();
            outputSearchViewItemList.addAll((List<SearchListViewItem>) results.values);
            notifyDataSetChanged();
        }
    };

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        itemClickListener = listener;
    }

    public interface OnFavouriteButtonClickListener {
        void onItemClick(int position);
    }

    public void setOnFavouriteButtonClickListener(OnFavouriteButtonClickListener listener) {
        favouriteButtonClickListener = listener;
    }

    static class SearchAdapterHolder extends RecyclerView.ViewHolder {

        AppCompatTextView textViewBusStopCodeSearch, textViewBusStopNameSearch, textViewBusStopAddressSearch;
        AppCompatImageButton imageButtonFavouriteSearch;

        SearchAdapterHolder(@NonNull View itemView, final OnItemClickListener listener, final OnFavouriteButtonClickListener listener2) {
            super(itemView);
            textViewBusStopCodeSearch = itemView.findViewById(R.id.textViewBusStopCodeSearch);
            textViewBusStopNameSearch = itemView.findViewById(R.id.textViewBusStopNameSearch);
            textViewBusStopAddressSearch = itemView.findViewById(R.id.textViewBusStopAddressSearch);
            imageButtonFavouriteSearch = itemView.findViewById(R.id.imageButtonFavouriteSearch);

            itemView.setOnClickListener((View v) -> {
                if (listener != null) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        listener.onItemClick(position);
                    }
                }
            });

            imageButtonFavouriteSearch.setOnClickListener((View v) -> {
                if (listener2 != null) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        listener2.onItemClick(position);
                    }
                }
            });
        }
    }
}
