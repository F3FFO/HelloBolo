package com.f3ffo.hellobolo.rss;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.text.HtmlCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.f3ffo.hellobolo.R;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textview.MaterialTextView;
import com.prof.rssparser.Article;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class ArticleAdapter extends RecyclerView.Adapter<ArticleAdapter.ArticleViewHolder> {

    private List<Article> articles;
    private Context context;

    public ArticleAdapter(List<Article> articles, Context context) {
        this.articles = articles;
        this.context = context;
    }

    public List<Article> getArticleList() {
        return articles;
    }

    @NonNull
    @Override
    public ArticleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.article_layout_item, parent, false);
        return new ArticleViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ArticleViewHolder viewHolder, int position) {
        Article currentArticle = articles.get(position);
        String articleDate;
        try {
            articleDate = new SimpleDateFormat("dd MMMM yyyy", Locale.ITALIAN).format(Objects.requireNonNull(new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss zzzz", Locale.ENGLISH).parse(currentArticle.getPubDate())));
        } catch (ParseException e) {
            e.printStackTrace();
            articleDate = currentArticle.getPubDate();
        }
        viewHolder.textViewArticleTitle.setText(currentArticle.getTitle());
        viewHolder.textViewArticleDate.setText(articleDate);
        StringBuilder categories = new StringBuilder();
        for (int i = 0; i < currentArticle.getCategories().size(); i++) {
            if (i == currentArticle.getCategories().size() - 1) {
                categories.append(currentArticle.getCategories().get(i));
            } else {
                categories.append(currentArticle.getCategories().get(i)).append(" - ");
            }
        }
        viewHolder.textViewArticleCategory.setText(categories.toString());
        viewHolder.itemView.setOnClickListener(view -> {
            MaterialTextView title = new MaterialTextView(context);
            title.setText(articles.get(viewHolder.getAdapterPosition()).getTitle());
            title.setTextAppearance(R.style.TextAppearance_MaterialComponents_Headline6_Custom);
            title.setPadding(64, 32, 64, 0);
            title.setTextIsSelectable(false);
            MaterialTextView description = new MaterialTextView(context);
            description.setText(HtmlCompat.fromHtml(Objects.requireNonNull(articles.get(viewHolder.getAdapterPosition()).getDescription()), HtmlCompat.FROM_HTML_MODE_LEGACY));
            description.setTextAppearance(R.style.TextAppearance_MaterialComponents_Body1_Custom);
            description.setPadding(64, 64, 64, 0);
            description.setTextIsSelectable(false);
            new MaterialAlertDialogBuilder(context, R.style.AlertDialogTheme)
                    .setCustomTitle(title)
                    .setView(description)
                    .setNegativeButton(R.string.alertDialog_rss_no, (DialogInterface dialog, int which) -> dialog.dismiss())
                    .setPositiveButton(R.string.alertDialog_rss_yes, (DialogInterface dialog, int which) -> {
                        Uri uri = Uri.parse(articles.get(viewHolder.getAdapterPosition()).getLink());
                        context.startActivity(new Intent(Intent.ACTION_VIEW, uri));
                    })
                    .show();
        });
    }

    @Override
    public int getItemCount() {
        return articles == null ? 0 : articles.size();
    }

    class ArticleViewHolder extends RecyclerView.ViewHolder {

        MaterialTextView textViewArticleDate, textViewArticleTitle, textViewArticleCategory;

        ArticleViewHolder(View itemView) {
            super(itemView);
            textViewArticleDate = itemView.findViewById(R.id.textViewArticleDate);
            textViewArticleTitle = itemView.findViewById(R.id.textViewArticleTitle);
            textViewArticleCategory = itemView.findViewById(R.id.textViewArticleCategory);
        }
    }
}