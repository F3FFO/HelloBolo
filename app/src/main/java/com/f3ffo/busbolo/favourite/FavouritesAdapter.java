package com.f3ffo.busbolo.favourite;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.recyclerview.widget.RecyclerView;

import com.f3ffo.busbolo.R;
import com.google.android.material.textview.MaterialTextView;

import java.util.List;

public class FavouritesAdapter extends RecyclerView.Adapter<FavouritesAdapter.FavouritesAdapterHolder> {

    private List<FavouritesItem> favouritesItemList;
    private OnItemClickListener itemClickListener;
    private OnFavouriteButtonClickListener favouriteButtonClickListener;

    public FavouritesAdapter(List<FavouritesItem> favouritesItemList) {
        this.favouritesItemList = favouritesItemList;
    }

    @NonNull
    @Override
    public FavouritesAdapterHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.favourite_list_item, parent, false);
        return new FavouritesAdapterHolder(view, itemClickListener, favouriteButtonClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull FavouritesAdapter.FavouritesAdapterHolder holder, int position) {
        FavouritesItem favouritesItem = favouritesItemList.get(position);
        holder.textViewBusStopCodeFavourite.setText(favouritesItem.getBusStopCode());
        holder.textViewBusStopNameFavourite.setText(favouritesItem.getBusStopName());
        holder.textViewBusStopAddressFavourite.setText(favouritesItem.getBusStopAddress());
    }

    @Override
    public int getItemCount() {
        return favouritesItemList.size();
    }

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

    static class FavouritesAdapterHolder extends RecyclerView.ViewHolder {

        MaterialTextView textViewBusStopCodeFavourite, textViewBusStopNameFavourite, textViewBusStopAddressFavourite;
        AppCompatImageButton imageButtonFavouriteSearch;

        FavouritesAdapterHolder(@NonNull View itemView, final OnItemClickListener listener, final OnFavouriteButtonClickListener listener2) {
            super(itemView);
            textViewBusStopCodeFavourite = itemView.findViewById(R.id.textViewBusStopCodeFavourite);
            textViewBusStopNameFavourite = itemView.findViewById(R.id.textViewBusStopNameFavourite);
            textViewBusStopAddressFavourite = itemView.findViewById(R.id.textViewBusStopAddressFavourite);
            imageButtonFavouriteSearch = itemView.findViewById(R.id.imageButtonFavourite);

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
