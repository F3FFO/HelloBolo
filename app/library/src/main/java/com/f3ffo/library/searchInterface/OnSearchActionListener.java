package com.f3ffo.library.searchInterface;

/**
 * Created by f3ffo on 17.01.2020.
 *
 * Interface definition for SearchBar callbacks.
 */
public interface OnSearchActionListener {
    /**
     * Invoked when SearchBar opened or closed
     *
     * @param enabled state
     */
    void onSearchStateChanged(boolean enabled);

    /**
     * Invoked when search confirmed and "search" button is clicked on the soft keyboard
     *
     * @param text search input
     */
    void onSearchConfirmed(CharSequence text);
}