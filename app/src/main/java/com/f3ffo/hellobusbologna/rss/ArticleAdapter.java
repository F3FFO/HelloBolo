package com.f3ffo.hellobusbologna.rss;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.f3ffo.hellobusbologna.R;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.prof.rssparser.Article;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;


public class ArticleAdapter extends RecyclerView.Adapter<ArticleAdapter.ArticleViewHolder> {

    private List<Article> articles;
    private Context context;

    public ArticleAdapter(List<Article> list, Context context) {
        this.articles = list;
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
            articleDate = new SimpleDateFormat("dd MMMM yyyy", Locale.ITALIAN).format(new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss Z", Locale.ENGLISH).parse(currentArticle.getPubDate()));
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
            WebView articleView = new WebView(context);
            articleView.getSettings().setLoadWithOverviewMode(true);
            //String title = articles.get(viewHolder.getAdapterPosition()).getTitle();
            String description = articles.get(viewHolder.getAdapterPosition()).getDescription();
            articleView.getSettings().setJavaScriptEnabled(false);
            articleView.getSettings().setGeolocationEnabled(false);
            articleView.setWebChromeClient(new WebChromeClient());
            //articleView.loadUrl(link);
            articleView.loadDataWithBaseURL(null, "<style>p {color: #494949; font-size: 1rem;}</style>" + description, "text/html", "UTF-8", null);

            AppCompatTextView title = new AppCompatTextView(context);
            title.setText(articles.get(viewHolder.getAdapterPosition()).getTitle());
            title.setTextColor(ContextCompat.getColor(context, R.color.colorGreyMaterial));
            title.setTextAppearance(R.style.TextAppearance_MaterialComponents_Headline6);
            title.setTextIsSelectable(false);

            new MaterialAlertDialogBuilder(context, R.style.Theme_MaterialComponents_Light_Dialog_Alert)
                    .setCustomTitle(title)
                    .setView(articleView)
                    .setNegativeButton("Chiudi", (DialogInterface dialog, int which) -> dialog.dismiss())
                    .setNeutralButton("Apri", (DialogInterface dialog, int which) -> {
                        Uri uri = Uri.parse(articles.get(viewHolder.getAdapterPosition()).getLink());
                        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                        context.startActivity(intent);
                    })
                    .show();

            /*AlertDialog alertDialog = new AlertDialog.Builder(context).create();
            alertDialog.setTitle(title);
            alertDialog.setView(articleView);
            alertDialog.setButton(Dialog.BUTTON_NEGATIVE, "Chiudi", (DialogInterface dialog, int which) -> dialog.dismiss());
            alertDialog.setButton(Dialog.BUTTON_NEUTRAL, "Apri", (DialogInterface dialog, int which) -> {
                Uri uri = Uri.parse(link);
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                context.startActivity(intent);
            });
            alertDialog.show();*/
        });
    }

    @Override
    public int getItemCount() {
        return articles == null ? 0 : articles.size();
    }

    class ArticleViewHolder extends RecyclerView.ViewHolder {

        AppCompatTextView textViewArticleDate, textViewArticleTitle, textViewArticleCategory;

        ArticleViewHolder(View itemView) {
            super(itemView);
            textViewArticleDate = itemView.findViewById(R.id.textViewArticleDate);
            textViewArticleTitle = itemView.findViewById(R.id.textViewArticleTitle);
            textViewArticleCategory = itemView.findViewById(R.id.textViewArticleCategory);
        }
    }
}