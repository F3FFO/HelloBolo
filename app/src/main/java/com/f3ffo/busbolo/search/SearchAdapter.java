package com.f3ffo.busbolo.search;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.recyclerview.widget.RecyclerView;

import com.f3ffo.busbolo.R;
import com.google.android.material.textview.MaterialTextView;

import java.util.ArrayList;
import java.util.List;

public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.SearchAdapterHolder> implements Filterable {

    private List<SearchItem> outputSearchViewItemList;
    private List<SearchItem> outputSearchViewItemListFull;
    private OnItemClickListener itemClickListener;
    private OnFavouriteButtonClickListener favouriteButtonClickListener;

    public SearchAdapter(List<SearchItem> outputSearchViewItemList) {
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
        SearchItem searchItem = outputSearchViewItemList.get(position);
        holder.textViewBusStopCodeSearch.setText(searchItem.getBusStopCode());
        holder.textViewBusStopNameSearch.setText(searchItem.getBusStopName());
        holder.textViewBusStopAddressSearch.setText(searchItem.getBusStopAddress());
        holder.imageButtonFavouriteSearch.setImageResource(searchItem.getImageFavourite());
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
            List<SearchItem> filteredItemList = new ArrayList<>();
            if (constraint == null || constraint.length() == 0) {
                filteredItemList.addAll(outputSearchViewItemListFull);
            } else {
                for (SearchItem item : outputSearchViewItemListFull) {
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
            outputSearchViewItemList.addAll((List<SearchItem>) results.values);
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

        MaterialTextView textViewBusStopCodeSearch, textViewBusStopNameSearch, textViewBusStopAddressSearch;
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
