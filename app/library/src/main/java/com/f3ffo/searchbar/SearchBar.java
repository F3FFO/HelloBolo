package com.f3ffo.searchbar;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.core.content.ContextCompat;

import com.google.android.material.card.MaterialCardView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textview.MaterialTextView;

import java.lang.reflect.Field;
import java.util.Objects;

/**
 * Created by F3FFO on 17.01.20.
 */

public class SearchBar extends RelativeLayout implements View.OnClickListener, Animation.AnimationListener, View.OnFocusChangeListener, MaterialTextView.OnEditorActionListener {
    private MaterialCardView cardViewContainer;
    private LinearLayoutCompat linearLayoutInputContainer;
    private ImageView imageViewSearch;
    private ImageView imageViewArrowBack;
    private ImageView imageViewClear;
    private ImageView imageViewExtra;
    private TextInputEditText editTextInputSearch;
    private MaterialTextView textViewPlaceholder;
    private OnSearchActionListener onSearchActionListener;
    private OnClickListener onExtraButtonClickListener;
    private boolean searchEnabled;
    private float destiny;
    private int arrowIconRes;
    private int clearIconRes;
    private int extraIconRes;
    private boolean navButtonEnabled;
    private int searchBarColor;
    private CharSequence hintText;
    private CharSequence placeholderText;
    private int textColor;
    private int hintColor;
    private int placeholderColor;
    private int radiusCorner;
    private int searchIconTint;
    private int arrowIconTint;
    private int clearIconTint;
    private int extraIconTint;
    private boolean searchIconTintEnabled;
    private boolean arrowIconTintEnabled;
    private boolean clearIconTintEnabled;
    private boolean extraIconTintEnabled;
    private int textCursorColor;
    private int highlightedTextColor;

    public SearchBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public SearchBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    public SearchBar(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(attrs);
    }

    private void init(AttributeSet attrs) {
        inflate(getContext(), R.layout.searchbar, SearchBar.this);
        TypedArray array = getContext().obtainStyledAttributes(attrs, R.styleable.SearchBar);
        //Base Attributes
        navButtonEnabled = array.getBoolean(R.styleable.SearchBar_mt_navIconEnabled, false);
        radiusCorner = array.getInt(R.styleable.SearchBar_mt_radiusCorner, 0);
        searchBarColor = array.getColor(R.styleable.SearchBar_mt_searchBarColor, ContextCompat.getColor(getContext(), R.color.searchBarPrimaryColor));
        //Icon Related Attributes
        arrowIconRes = array.getResourceId(R.styleable.SearchBar_mt_backIconDrawable, R.drawable.arrow_back);
        clearIconRes = array.getResourceId(R.styleable.SearchBar_mt_clearIconDrawable, R.drawable.close);
        extraIconRes = array.getResourceId(R.styleable.SearchBar_mt_extraIconDrawable, 0);
        searchIconTint = array.getColor(R.styleable.SearchBar_mt_searchIconTint, ContextCompat.getColor(getContext(), R.color.searchBarSearchIconTintColor));
        arrowIconTint = array.getColor(R.styleable.SearchBar_mt_backIconTint, ContextCompat.getColor(getContext(), R.color.searchBarBackIconTintColor));
        clearIconTint = array.getColor(R.styleable.SearchBar_mt_clearIconTint, ContextCompat.getColor(getContext(), R.color.searchBarClearIconTintColor));
        extraIconTint = array.getColor(R.styleable.SearchBar_mt_extraIconTint, ContextCompat.getColor(getContext(), R.color.searchBarExtraIconTintColor));
        searchIconTintEnabled = array.getBoolean(R.styleable.SearchBar_mt_searchIconUseTint, true);
        arrowIconTintEnabled = array.getBoolean(R.styleable.SearchBar_mt_backIconUseTint, true);
        clearIconTintEnabled = array.getBoolean(R.styleable.SearchBar_mt_clearIconUseTint, true);
        extraIconTintEnabled = array.getBoolean(R.styleable.SearchBar_mt_extraIconUseTint, true);
        //Text Related Attributes
        hintText = array.getString(R.styleable.SearchBar_mt_hint);
        placeholderText = array.getString(R.styleable.SearchBar_mt_placeholder);
        textColor = array.getColor(R.styleable.SearchBar_mt_textColor, ContextCompat.getColor(getContext(), R.color.searchBarTextColor));
        hintColor = array.getColor(R.styleable.SearchBar_mt_hintColor, ContextCompat.getColor(getContext(), R.color.searchBarHintColor));
        placeholderColor = array.getColor(R.styleable.SearchBar_mt_placeholderColor, ContextCompat.getColor(getContext(), R.color.searchBarPlaceholderColor));
        textCursorColor = array.getColor(R.styleable.SearchBar_mt_textCursorTint, ContextCompat.getColor(getContext(), R.color.searchBarCursorColor));
        highlightedTextColor = array.getColor(R.styleable.SearchBar_mt_highlightedTextColor, ContextCompat.getColor(getContext(), R.color.searchBarTextHighlightColor));
        destiny = getResources().getDisplayMetrics().density;
        array.recycle();
        //View References
        cardViewContainer = findViewById(R.id.cardViewContainer);
        imageViewExtra = findViewById(R.id.imageViewExtra);
        imageViewClear = findViewById(R.id.imageViewClear);
        imageViewSearch = findViewById(R.id.imageViewSearch);
        imageViewArrowBack = findViewById(R.id.imageViewArrowBack);
        editTextInputSearch = findViewById(R.id.editTextInputSearch);
        textViewPlaceholder = findViewById(R.id.textViewPlaceholder);
        linearLayoutInputContainer = findViewById(R.id.linearLayoutInputContainer);
        findViewById(R.id.imageViewClear).setOnClickListener(SearchBar.this);
        findViewById(R.id.imageViewExtra).setOnClickListener(SearchBar.this);
        //Listeners
        setOnClickListener(SearchBar.this);
        imageViewArrowBack.setOnClickListener(SearchBar.this);
        imageViewSearch.setOnClickListener(SearchBar.this);
        editTextInputSearch.setOnFocusChangeListener(SearchBar.this);
        editTextInputSearch.setOnEditorActionListener(SearchBar.this);
        postSetup();
    }

    private void postSetup() {
        setupTextColors();
        setupRadiusSearchBar();
        setupSearchBarColor();
        setupIcons();
        setupSearchEditText();
    }

    /**
     * Capsule shaped search bar enabled
     */
    private void setupRadiusSearchBar() {
        cardViewContainer.setRadius(radiusCorner);
    }

    private void setupSearchBarColor() {
        cardViewContainer.setCardBackgroundColor(searchBarColor);
    }

    private void setupTextColors() {
        editTextInputSearch.setHintTextColor(hintColor);
        editTextInputSearch.setTextColor(textColor);
        textViewPlaceholder.setTextColor(placeholderColor);
    }

    /**
     * Setup editText coloring and drawables
     */
    private void setupSearchEditText() {
        setupCursorColor();
        editTextInputSearch.setHighlightColor(highlightedTextColor);
        if (hintText != null) {
            editTextInputSearch.setHint(hintText);
        }
        if (placeholderText != null) {
            imageViewArrowBack.setBackground(null);
            textViewPlaceholder.setText(placeholderText);
        }
    }

    private void setupCursorColor() {
        try {
            Field field = MaterialTextView.class.getDeclaredField("editor");
            field.setAccessible(true);
            Object editor = field.get(editTextInputSearch);
            field = MaterialTextView.class.getDeclaredField("cursorDrawableRes");
            field.setAccessible(true);
            int cursorDrawableRes = field.getInt(editTextInputSearch);
            Drawable cursorDrawable = Objects.requireNonNull(ContextCompat.getDrawable(getContext(), cursorDrawableRes)).mutate();
            cursorDrawable.setColorFilter(textCursorColor, PorterDuff.Mode.SRC_IN);
            Drawable[] drawables = {cursorDrawable, cursorDrawable};
            field = Objects.requireNonNull(editor).getClass().getDeclaredField("cursorDrawable");
            field.setAccessible(true);
            field.set(editor, drawables);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    //Setup Icon Colors And Drawables
    private void setupIcons() {
        //Drawables
        setNavButtonEnabled(navButtonEnabled);
        //Arrow
        imageViewArrowBack.setImageResource(arrowIconRes);
        //Clear
        imageViewClear.setImageResource(clearIconRes);
        //Extra
        imageViewExtra.setImageResource(extraIconRes);
        //Colors
        setupSearchIconTint();
        setupArrowIconTint();
        setupClearIconTint();
        setupExtraIconTint();
        setupIconRippleStyle();
    }

    private void setupSearchIconTint() {
        if (searchIconTintEnabled) {
            imageViewSearch.setColorFilter(searchIconTint, PorterDuff.Mode.SRC_IN);
        } else {
            imageViewSearch.clearColorFilter();
        }
    }

    private void setupArrowIconTint() {
        if (arrowIconTintEnabled) {
            imageViewArrowBack.setColorFilter(arrowIconTint, PorterDuff.Mode.SRC_IN);
        } else {
            imageViewArrowBack.clearColorFilter();
        }
    }

    private void setupClearIconTint() {
        if (clearIconTintEnabled) {
            imageViewClear.setColorFilter(clearIconTint, PorterDuff.Mode.SRC_IN);
        } else {
            imageViewClear.clearColorFilter();
        }
    }

    private void setupExtraIconTint() {
        if (extraIconTintEnabled) {
            imageViewExtra.setColorFilter(extraIconTint, PorterDuff.Mode.SRC_IN);
        } else {
            imageViewExtra.clearColorFilter();
        }
    }

    private void setupIconRippleStyle() {
        TypedValue rippleStyle = new TypedValue();
        getContext().getTheme().resolveAttribute(android.R.attr.selectableItemBackgroundBorderless, rippleStyle, true);
        imageViewSearch.setBackgroundResource(rippleStyle.resourceId);
        imageViewArrowBack.setBackgroundResource(rippleStyle.resourceId);
        imageViewClear.setBackgroundResource(rippleStyle.resourceId);
        imageViewExtra.setBackgroundResource(rippleStyle.resourceId);
    }

    /**
     * Register listener for search bar callbacks.
     *
     * @param onSearchActionListener the callback listener
     */
    public void setOnSearchActionListener(OnSearchActionListener onSearchActionListener) {
        this.onSearchActionListener = onSearchActionListener;
    }

    public void setOnExtraButtonClickListener(OnClickListener onClickListener) {
        this.onExtraButtonClickListener = onClickListener;
    }

    /**
     * Hides search input and close arrow
     */
    public void disableSearch() {
        this.searchEnabled = false;
        Animation out = AnimationUtils.loadAnimation(getContext(), R.anim.fade_out);
        Animation in = AnimationUtils.loadAnimation(getContext(), R.anim.fade_in_right);
        out.setAnimationListener(SearchBar.this);
        this.imageViewSearch.setVisibility(VISIBLE);
        this.linearLayoutInputContainer.startAnimation(out);
        this.imageViewSearch.startAnimation(in);
        if (placeholderText != null) {
            this.textViewPlaceholder.setVisibility(VISIBLE);
            this.textViewPlaceholder.startAnimation(in);
        }
        if (listenerExists()) {
            this.onSearchActionListener.onSearchStateChanged(false);
        }
    }

    /**
     * Shows search input and close arrow
     */
    public void enableSearch() {
        if (isSearchEnabled()) {
            this.onSearchActionListener.onSearchStateChanged(true);
            this.editTextInputSearch.requestFocus();
            return;
        }
        this.searchEnabled = true;
        Animation left_in = AnimationUtils.loadAnimation(getContext(), R.anim.fade_in_left);
        Animation left_out = AnimationUtils.loadAnimation(getContext(), R.anim.fade_out_left);
        left_in.setAnimationListener(SearchBar.this);
        this.textViewPlaceholder.setVisibility(GONE);
        this.linearLayoutInputContainer.setVisibility(VISIBLE);
        this.linearLayoutInputContainer.startAnimation(left_in);
        if (listenerExists()) {
            this.onSearchActionListener.onSearchStateChanged(true);
        }
        this.imageViewSearch.startAnimation(left_out);
    }

    /**
     * Set back arrow icon drawable
     *
     * @param arrowIconResId icon resource id
     */
    public void setArrowIcon(int arrowIconResId) {
        this.arrowIconRes = arrowIconResId;
        this.imageViewArrowBack.setImageResource(arrowIconRes);
    }

    /**
     * Set clear icon drawable
     *
     * @param clearIconResId icon resource id
     */
    public void setClearIcon(int clearIconResId) {
        this.clearIconRes = clearIconResId;
        this.imageViewClear.setImageResource(clearIconRes);
    }

    /**
     * Set extra icon drawable
     *
     * @param extraIconResId icon resource id
     */
    public void setExtraIcon(int extraIconResId) {
        this.extraIconRes = extraIconResId;
        this.imageViewExtra.setImageResource(extraIconRes);
        this.imageViewExtra.setVisibility(VISIBLE);
    }

    /**
     * Set the tint color of the search/speech icon
     *
     * @param searchIconTint search icon color
     */
    public void setSearchIconTint(int searchIconTint) {
        this.searchIconTint = searchIconTint;
        setupSearchIconTint();
    }

    /**
     * Set the tint color of the back arrow icon
     *
     * @param arrowIconTint arrow icon color
     */
    public void setArrowIconTint(int arrowIconTint) {
        this.arrowIconTint = arrowIconTint;
        setupArrowIconTint();
    }

    /**
     * Set the tint color of the clear icon
     *
     * @param clearIconTint clear icon tint
     */
    public void setClearIconTint(int clearIconTint) {
        this.clearIconTint = clearIconTint;
        setupClearIconTint();
    }

    /**
     * Set the tint color of the clear icon
     *
     * @param extraIconTint extra icon tint
     */
    public void setExtraIconTint(int extraIconTint) {
        this.extraIconTint = extraIconTint;
        setupExtraIconTint();
    }

    /**
     * Set search bar hintText
     *
     * @param hintText hintText text
     */
    public void setHint(CharSequence hintText) {
        this.hintText = hintText;
        editTextInputSearch.setHint(hintText);
    }

    /**
     * Get the place holder text
     *
     * @return placeholder text
     */
    public CharSequence getPlaceHolderText() {
        return textViewPlaceholder.getText();
    }

    /**
     * Check if search bar is in edit mode
     *
     * @return true if search bar is in edit mode
     */
    public boolean isSearchEnabled() {
        return searchEnabled;
    }

    /**
     * Set search input text color
     *
     * @param textColor text color
     */
    public void setTextColor(int textColor) {
        this.textColor = textColor;
        setupTextColors();
    }

    /**
     * Set text input hintText color
     *
     * @param hintColor text hintText color
     */
    public void setTextHintColor(int hintColor) {
        this.hintColor = hintColor;
        setupTextColors();
    }

    /**
     * Set placeholder text color
     *
     * @param placeholderColor placeholder color
     */
    public void setPlaceHolderColor(int placeholderColor) {
        this.placeholderColor = placeholderColor;
        setupTextColors();
    }

    /**
     * Set text appearance for placeHolder
     *
     * @param context        of activity
     * @param textAppearance id of style to apply
     */
    public void setPlaceholderTextAppearance(Context context, int textAppearance) {
        this.textViewPlaceholder.setTextAppearance(context, textAppearance);
    }

    /**
     * Set the color of the highlight when text is selected
     *
     * @param highlightedTextColor selected text highlight color
     */
    public void setTextHighlightColor(int highlightedTextColor) {
        this.highlightedTextColor = highlightedTextColor;
        editTextInputSearch.setHighlightColor(highlightedTextColor);
    }

    /**
     * Set navigation drawer menu icon enabled
     *
     * @param navButtonEnabled icon enabled
     */
    public void setNavButtonEnabled(boolean navButtonEnabled) {
        this.navButtonEnabled = navButtonEnabled;
        if (navButtonEnabled) {
            ((LayoutParams) linearLayoutInputContainer.getLayoutParams()).leftMargin = (int) (50 * this.destiny);
            this.imageViewArrowBack.setVisibility(GONE);
        } else {
            ((LayoutParams) linearLayoutInputContainer.getLayoutParams()).leftMargin = (int) (0 * this.destiny);
            this.imageViewArrowBack.setVisibility(VISIBLE);
        }
        this.textViewPlaceholder.requestLayout();
        this.imageViewArrowBack.requestLayout();
    }

    /**
     * Set radius corner SearchBar
     *
     * @param radius desired radius in pixels of the corners
     */
    public void setRadiusSearchBar(int radius) {
        this.radiusCorner = radius;
        setupRadiusSearchBar();
    }

    /**
     * Set CardView elevation
     *
     * @param elevation desired elevation
     */
    public void setCardViewElevation(int elevation) {
        MaterialCardView cardView = findViewById(R.id.cardViewContainer);
        cardView.setCardElevation(elevation);
    }

    /**
     * Get search text
     *
     * @return text
     */
    public String getText() {
        return Objects.requireNonNull(editTextInputSearch.getText()).toString();
    }

    /**
     * Set search text
     *
     * @param text text
     */
    public void setText(String text) {
        editTextInputSearch.setText(text);
    }

    /**
     * Add text watcher to searchbarActivity's EditText
     *
     * @param textWatcher textWatcher to add
     */
    public void addTextChangeListener(TextWatcher textWatcher) {
        editTextInputSearch.addTextChangedListener(textWatcher);
    }

    public MaterialTextView getPlaceHolderView() {
        return textViewPlaceholder;
    }

    /**
     * Set the place holder text
     *
     * @param placeholder placeholder text
     */
    public void setPlaceHolder(CharSequence placeholder) {
        this.placeholderText = placeholder;
        this.textViewPlaceholder.setText(placeholder);
    }

    private boolean listenerExists() {
        return onSearchActionListener != null && onExtraButtonClickListener != null;
    }

    @Override
    public void onClick(@NonNull View v) {
        int id = v.getId();
        if (id == getId() || id == R.id.imageViewSearch) {
            if (!searchEnabled) {
                enableSearch();
            }
        } else if (id == R.id.imageViewArrowBack) {
            disableSearch();
        } else if (id == R.id.imageViewClear) {
            editTextInputSearch.setText("");
        } else if (id == R.id.imageViewExtra) {
            onExtraButtonClickListener.onClick(v);
        }
    }

    @Override
    public void onAnimationStart(Animation animation) {
    }

    @Override
    public void onAnimationEnd(Animation animation) {
        if (!searchEnabled) {
            linearLayoutInputContainer.setVisibility(GONE);
            editTextInputSearch.setText("");
        } else {
            imageViewSearch.setVisibility(GONE);
            editTextInputSearch.requestFocus();
        }
    }

    @Override
    public void onAnimationRepeat(Animation animation) {
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        if (hasFocus) {
            Objects.requireNonNull(imm).showSoftInput(v, InputMethodManager.SHOW_IMPLICIT);
        } else {
            Objects.requireNonNull(imm).hideSoftInputFromWindow(v.getWindowToken(), 0);
        }
    }

    @Override
    public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
        if (listenerExists()) {
            onSearchActionListener.onSearchConfirmed(editTextInputSearch.getText());
        }
        return true;
    }

    /**
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
}