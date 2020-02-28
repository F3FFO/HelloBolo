package com.f3ffo.library;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.core.content.ContextCompat;

import com.f3ffo.library.searchInterface.OnSearchActionListener;
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
    private AppCompatImageView imageViewSearch, imageViewArrowBack, imageViewClear, imageViewExtra;
    private TextInputEditText editTextInputSearch;
    private MaterialTextView textViewPlaceholder;
    private OnSearchActionListener onSearchActionListener;
    private OnClickListener onExtraButtonClickListener;
    private float destiny;
    private CharSequence hintText, placeholderText;
    private int arrowIconRes, clearIconRes, extraIconRes, searchBarColor, textColor, hintColor, placeholderColor, radiusCorner, searchIconTint, arrowIconTint, clearIconTint, extraIconTint, textCursorColor, highlightedTextColor;
    private boolean searchEnabled, navButtonEnabled, searchIconTintEnabled, arrowIconTintEnabled, clearIconTintEnabled, extraIconTintEnabled;

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
        this.navButtonEnabled = array.getBoolean(R.styleable.SearchBar_mt_navIconEnabled, false);
        this.radiusCorner = array.getInt(R.styleable.SearchBar_mt_radiusCorner, 0);
        this.searchBarColor = array.getColor(R.styleable.SearchBar_mt_searchBarColor, ContextCompat.getColor(getContext(), R.color.searchBarPrimaryColor));
        //Icon Related Attributes
        this.arrowIconRes = array.getResourceId(R.styleable.SearchBar_mt_backIconDrawable, R.drawable.arrow_back);
        this.clearIconRes = array.getResourceId(R.styleable.SearchBar_mt_clearIconDrawable, R.drawable.close);
        this.extraIconRes = array.getResourceId(R.styleable.SearchBar_mt_extraIconDrawable, 0);
        this.searchIconTint = array.getColor(R.styleable.SearchBar_mt_searchIconTint, ContextCompat.getColor(getContext(), R.color.searchBarSearchIconTintColor));
        this.arrowIconTint = array.getColor(R.styleable.SearchBar_mt_backIconTint, ContextCompat.getColor(getContext(), R.color.searchBarBackIconTintColor));
        this.clearIconTint = array.getColor(R.styleable.SearchBar_mt_clearIconTint, ContextCompat.getColor(getContext(), R.color.searchBarClearIconTintColor));
        this.extraIconTint = array.getColor(R.styleable.SearchBar_mt_extraIconTint, ContextCompat.getColor(getContext(), R.color.searchBarExtraIconTintColor));
        this.searchIconTintEnabled = array.getBoolean(R.styleable.SearchBar_mt_searchIconUseTint, true);
        this.arrowIconTintEnabled = array.getBoolean(R.styleable.SearchBar_mt_backIconUseTint, true);
        this.clearIconTintEnabled = array.getBoolean(R.styleable.SearchBar_mt_clearIconUseTint, true);
        this.extraIconTintEnabled = array.getBoolean(R.styleable.SearchBar_mt_extraIconUseTint, true);
        //Text Related Attributes
        this.hintText = array.getString(R.styleable.SearchBar_mt_hint);
        this.placeholderText = array.getString(R.styleable.SearchBar_mt_placeholder);
        this.textColor = array.getColor(R.styleable.SearchBar_mt_textColor, ContextCompat.getColor(getContext(), R.color.searchBarTextColor));
        this.hintColor = array.getColor(R.styleable.SearchBar_mt_hintColor, ContextCompat.getColor(getContext(), R.color.searchBarHintColor));
        this.placeholderColor = array.getColor(R.styleable.SearchBar_mt_placeholderColor, ContextCompat.getColor(getContext(), R.color.searchBarPlaceholderColor));
        this.textCursorColor = array.getColor(R.styleable.SearchBar_mt_textCursorTint, ContextCompat.getColor(getContext(), R.color.searchBarCursorColor));
        this.highlightedTextColor = array.getColor(R.styleable.SearchBar_mt_highlightedTextColor, ContextCompat.getColor(getContext(), R.color.searchBarTextHighlightColor));
        this.destiny = getResources().getDisplayMetrics().density;
        array.recycle();
        //View References
        this.cardViewContainer = findViewById(R.id.cardViewContainer);
        this.imageViewExtra = findViewById(R.id.imageViewExtra);
        this.imageViewClear = findViewById(R.id.imageViewClear);
        this.imageViewSearch = findViewById(R.id.imageViewSearch);
        this.imageViewArrowBack = findViewById(R.id.imageViewArrowBack);
        this.editTextInputSearch = findViewById(R.id.editTextInputSearch);
        this.textViewPlaceholder = findViewById(R.id.textViewPlaceholder);
        this.linearLayoutInputContainer = findViewById(R.id.linearLayoutInputContainer);
        findViewById(R.id.imageViewClear).setOnClickListener(SearchBar.this);
        findViewById(R.id.imageViewExtra).setOnClickListener(SearchBar.this);
        //Listeners
        setOnClickListener(SearchBar.this);
        this.imageViewArrowBack.setOnClickListener(SearchBar.this);
        this.imageViewSearch.setOnClickListener(SearchBar.this);
        this.editTextInputSearch.setOnFocusChangeListener(SearchBar.this);
        this.editTextInputSearch.setOnEditorActionListener(SearchBar.this);
        postSetup();
    }

    private void postSetup() {
        setupTextColors();
        setupRadiusSearchBar();
        setupSearchBarColor();
        setupIcons();
        setupSearchEditText();
    }

    private void setupRadiusSearchBar() {
        this.cardViewContainer.setRadius(this.radiusCorner);
    }

    private void setupSearchBarColor() {
        this.cardViewContainer.setCardBackgroundColor(this.searchBarColor);
    }

    private void setupTextColors() {
        this.editTextInputSearch.setHintTextColor(this.hintColor);
        this.editTextInputSearch.setTextColor(this.textColor);
        this.textViewPlaceholder.setTextColor(this.placeholderColor);
    }

    /**
     * Setup editText coloring and drawables
     */
    private void setupSearchEditText() {
        setupCursorColor();
        this.editTextInputSearch.setHighlightColor(this.highlightedTextColor);
        if (this.hintText != null) {
            this.editTextInputSearch.setHint(this.hintText);
        }
        if (this.placeholderText != null) {
            this.imageViewArrowBack.setBackground(null);
            this.textViewPlaceholder.setText(this.placeholderText);
        }
    }

    private void setupCursorColor() {
        try {
            Field field = MaterialTextView.class.getDeclaredField("mCursorDrawableRes");
            field.setAccessible(true);
            int drawableResId = field.getInt(this.editTextInputSearch);
            // Get the editor
            field = MaterialTextView.class.getDeclaredField("mEditor");
            field.setAccessible(true);
            Object editor = field.get(this.editTextInputSearch);
            // Get the drawable and set a color filter
            Drawable drawable = ContextCompat.getDrawable(this.editTextInputSearch.getContext(), drawableResId);
            Objects.requireNonNull(drawable).setColorFilter(this.textCursorColor, PorterDuff.Mode.SRC_IN);
            // Set the drawables
            if (Build.VERSION.SDK_INT >= 28) {
                field = Objects.requireNonNull(editor).getClass().getDeclaredField("mDrawableForCursor");
                field.setAccessible(true);
                field.set(editor, drawable);
            } else {
                Drawable[] drawables = {drawable, drawable};
                field = Objects.requireNonNull(editor).getClass().getDeclaredField("mCursorDrawable");
                field.setAccessible(true);
                field.set(editor, drawables);
            }
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    //Setup Icon Colors And Drawables
    private void setupIcons() {
        setNavButtonEnabled(this.navButtonEnabled);
        this.imageViewArrowBack.setImageResource(this.arrowIconRes);
        this.imageViewClear.setImageResource(this.clearIconRes);
        this.imageViewExtra.setImageResource(this.extraIconRes);
        setupSearchIconTint();
        setupArrowIconTint();
        setupClearIconTint();
        setupExtraIconTint();
        setupIconRippleStyle();
    }

    private void setupSearchIconTint() {
        if (this.searchIconTintEnabled) {
            this.imageViewSearch.setColorFilter(this.searchIconTint, PorterDuff.Mode.SRC_IN);
        } else {
            this.imageViewSearch.clearColorFilter();
        }
    }

    private void setupArrowIconTint() {
        if (this.arrowIconTintEnabled) {
            this.imageViewArrowBack.setColorFilter(this.arrowIconTint, PorterDuff.Mode.SRC_IN);
        } else {
            this.imageViewArrowBack.clearColorFilter();
        }
    }

    private void setupClearIconTint() {
        if (this.clearIconTintEnabled) {
            this.imageViewClear.setColorFilter(clearIconTint, PorterDuff.Mode.SRC_IN);
        } else {
            this.imageViewClear.clearColorFilter();
        }
    }

    private void setupExtraIconTint() {
        if (this.extraIconTintEnabled) {
            this.imageViewExtra.setColorFilter(this.extraIconTint, PorterDuff.Mode.SRC_IN);
        } else {
            this.imageViewExtra.clearColorFilter();
        }
    }

    private void setupIconRippleStyle() {
        TypedValue rippleStyle = new TypedValue();
        getContext().getTheme().resolveAttribute(android.R.attr.selectableItemBackgroundBorderless, rippleStyle, true);
        this.imageViewSearch.setBackgroundResource(rippleStyle.resourceId);
        this.imageViewArrowBack.setBackgroundResource(rippleStyle.resourceId);
        this.imageViewClear.setBackgroundResource(rippleStyle.resourceId);
        this.imageViewExtra.setBackgroundResource(rippleStyle.resourceId);
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
        if (this.placeholderText != null) {
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
        this.imageViewArrowBack.setImageResource(this.arrowIconRes);
    }

    /**
     * Set clear icon drawable
     *
     * @param clearIconResId icon resource id
     */
    public void setClearIcon(int clearIconResId) {
        this.clearIconRes = clearIconResId;
        this.imageViewClear.setImageResource(this.clearIconRes);
    }

    /**
     * Set extra icon drawable
     *
     * @param extraIconResId icon resource id
     */
    public void setExtraIcon(int extraIconResId) {
        this.extraIconRes = extraIconResId;
        this.imageViewExtra.setImageResource(this.extraIconRes);
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
        this.editTextInputSearch.setHint(hintText);
    }

    /**
     * Get the place holder text
     *
     * @return placeholder text
     */
    public CharSequence getPlaceHolderText() {
        return this.textViewPlaceholder.getText();
    }

    /**
     * Check if search bar is in edit mode
     *
     * @return true if search bar is in edit mode
     */
    public boolean isSearchEnabled() {
        return this.searchEnabled;
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
        this.editTextInputSearch.setHighlightColor(highlightedTextColor);
    }

    /**
     * Set navigation drawer menu icon enabled
     *
     * @param navButtonEnabled icon enabled
     */
    public void setNavButtonEnabled(boolean navButtonEnabled) {
        this.navButtonEnabled = navButtonEnabled;
        if (navButtonEnabled) {
            ((LayoutParams) this.linearLayoutInputContainer.getLayoutParams()).leftMargin = (int) (50 * this.destiny);
            this.imageViewArrowBack.setVisibility(GONE);
        } else {
            ((LayoutParams) this.linearLayoutInputContainer.getLayoutParams()).leftMargin = (int) (0 * this.destiny);
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
        return Objects.requireNonNull(this.editTextInputSearch.getText()).toString();
    }

    /**
     * Set search text
     *
     * @param text text
     */
    public void setText(String text) {
        this.editTextInputSearch.setText(text);
    }

    /**
     * Add text watcher to searchbarActivity's EditText
     *
     * @param textWatcher textWatcher to add
     */
    public void addTextChangeListener(TextWatcher textWatcher) {
        this.editTextInputSearch.addTextChangedListener(textWatcher);
    }

    public MaterialTextView getPlaceHolderView() {
        return this.textViewPlaceholder;
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
        return this.onSearchActionListener != null;
    }

    @Override
    public void onClick(@NonNull View v) {
        int id = v.getId();
        if (id == getId() || id == R.id.imageViewSearch) {
            if (!this.searchEnabled) {
                enableSearch();
            }
        } else if (id == R.id.imageViewArrowBack) {
            disableSearch();
        } else if (id == R.id.imageViewClear) {
            this.editTextInputSearch.setText("");
        } else if (id == R.id.imageViewExtra) {
            this.onExtraButtonClickListener.onClick(v);
        }
    }

    @Override
    public void onAnimationStart(Animation animation) {
    }

    @Override
    public void onAnimationEnd(Animation animation) {
        if (!this.searchEnabled) {
            this.linearLayoutInputContainer.setVisibility(GONE);
            this.editTextInputSearch.setText("");
        } else {
            imageViewSearch.setVisibility(GONE);
            this.editTextInputSearch.requestFocus();
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
            this.onSearchActionListener.onSearchConfirmed(this.editTextInputSearch.getText());
        }
        return true;
    }
}