package com.f3ffo.hellobusbologna.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.RecyclerView;

import com.f3ffo.hellobusbologna.R;
import com.f3ffo.hellobusbologna.model.FavouritesViewItem;

import java.util.List;

public class FavouritesAdapter extends RecyclerView.Adapter<FavouritesAdapter.FavouritesAdapterHolder> {

    private List<FavouritesViewItem> favouritesViewItemList;
    private OnItemClickListener itemClickListener;
    private OnFavouriteButtonClickListener favouriteButtonClickListener;

    public FavouritesAdapter(List<FavouritesViewItem> favouritesViewItemList) {
        this.favouritesViewItemList = favouritesViewItemList;
    }

    @NonNull
    @Override
    public FavouritesAdapterHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.favourite_list_item, parent, false);
        return new FavouritesAdapterHolder(view, itemClickListener, favouriteButtonClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull FavouritesAdapter.FavouritesAdapterHolder holder, int position) {
        FavouritesViewItem favouritesViewItem = favouritesViewItemList.get(position);
        holder.textViewBusStopCodeFavourite.setText(favouritesViewItem.getBusStopCode());
        holder.textViewBusStopNameFavourite.setText(favouritesViewItem.getBusStopName());
        holder.textViewBusStopAddressFavourite.setText(favouritesViewItem.getBusStopAddress());
    }

    @Override
    public int getItemCount() {
        return favouritesViewItemList.size();
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

        AppCompatTextView textViewBusStopCodeFavourite, textViewBusStopNameFavourite, textViewBusStopAddressFavourite;
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
