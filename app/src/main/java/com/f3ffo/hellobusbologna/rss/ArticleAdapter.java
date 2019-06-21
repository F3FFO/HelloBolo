package com.f3ffo.hellobusbologna.rss;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.RecyclerView;

import com.f3ffo.hellobusbologna.R;
import com.prof.rssparser.Article;
import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;


public class ArticleAdapter extends RecyclerView.Adapter<ArticleAdapter.ArticleViewHolder> {

    private List<Article> articles;
    private Context context;
    private WebView articleView;

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
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row, parent, false);
        return new ArticleViewHolder(view);
    }

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    public void onBindViewHolder(@NonNull final ArticleViewHolder viewHolder, int position) {
        Article currentArticle = articles.get(position);
        String pubDateString;
        try {
            String sourceDateString = currentArticle.getPubDate();
            Date date = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss Z", Locale.ENGLISH).parse(sourceDateString);
            pubDateString = new SimpleDateFormat("dd MMMM yyyy", Locale.ITALIAN).format(date);
        } catch (ParseException e) {
            Log.e("ERROR ArticleAdapter", e.getMessage());
            e.printStackTrace();
            pubDateString = currentArticle.getPubDate();
        }
        viewHolder.title.setText(currentArticle.getTitle());
        Picasso.get()
                .load(currentArticle.getImage())
                .placeholder(R.drawable.placeholder)
                .into(viewHolder.image);
        viewHolder.pubDate.setText(pubDateString);
        StringBuilder categories = new StringBuilder();
        for (int i = 0; i < currentArticle.getCategories().size(); i++) {
            if (i == currentArticle.getCategories().size() - 1) {
                categories.append(currentArticle.getCategories().get(i));
            } else {
                categories.append(currentArticle.getCategories().get(i)).append(" - ");
            }
        }
        viewHolder.category.setText(categories.toString());
        viewHolder.itemView.setOnClickListener(view -> {
            articleView = new WebView(context);
            articleView.getSettings().setLoadWithOverviewMode(true);
            String title = articles.get(viewHolder.getAdapterPosition()).getTitle();
            String description = articles.get(viewHolder.getAdapterPosition()).getDescription();
            String link = articles.get(viewHolder.getAdapterPosition()).getLink();
            articleView.getSettings().setJavaScriptEnabled(false);
            articleView.getSettings().setGeolocationEnabled(false);
            articleView.setHorizontalScrollBarEnabled(false);
            articleView.setWebChromeClient(new WebChromeClient());
            //articleView.loadUrl(link);
            articleView.loadDataWithBaseURL(null, "<style>.field-item { margin-left: 20px; margin-right: 20px;}</style>\n" + description, "text/html", "UTF-8", null);
            AlertDialog alertDialog = new AlertDialog.Builder(context).create();
            alertDialog.setTitle(title);
            alertDialog.setView(articleView);
            alertDialog.setButton(Dialog.BUTTON_NEGATIVE, "Chiudi", (DialogInterface dialog, int which) -> dialog.dismiss());
            alertDialog.setButton(Dialog.BUTTON_NEUTRAL, "Apri", (DialogInterface dialog, int which) -> {
                Uri uri = Uri.parse(link);
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                context.startActivity(intent);
            });
            alertDialog.show();
        });
    }

    @Override
    public int getItemCount() {
        return articles == null ? 0 : articles.size();
    }

    class ArticleViewHolder extends RecyclerView.ViewHolder {

        AppCompatTextView title, pubDate, category;
        AppCompatImageView image;

        ArticleViewHolder(View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.title);
            pubDate = itemView.findViewById(R.id.pubDate);
            image = itemView.findViewById(R.id.image);
            category = itemView.findViewById(R.id.categories);
        }
    }
}