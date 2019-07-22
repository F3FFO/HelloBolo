package com.f3ffo.hellobusbologna.rss;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.f3ffo.hellobusbologna.R;
import com.google.android.material.snackbar.Snackbar;
import com.prof.rssparser.Article;

import java.util.List;

public class ArticleFragment extends Fragment {

    private static final String ARG_SECTION_NUMBER = "";
    private ArticleViewModel articleViewModel;
    private ProgressBar progressBarRss;
    private SwipeRefreshLayout swipeRefreshLayoutRss;
    private ArticleAdapter articleAdapter;
    private RecyclerView recyclerViewRss;
    private ConstraintLayout constraintLayoutRss;

    public static ArticleFragment newInstance(int index) {
        ArticleFragment fragment = new ArticleFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(ARG_SECTION_NUMBER, index);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        articleViewModel = ViewModelProviders.of(this).get(ArticleViewModel.class);
        int index = 1;
        if (getArguments() != null) {
            index = getArguments().getInt(ARG_SECTION_NUMBER);
        }
        articleViewModel.setIndex(index);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_rss, container, false);
        constraintLayoutRss = root.findViewById(R.id.constraintLayoutRss);
        progressBarRss = root.findViewById(R.id.progressBarRss);
        recyclerViewRss = root.findViewById(R.id.recyclerViewRss);
        swipeRefreshLayoutRss = root.findViewById(R.id.swipeRefreshLayoutRss);
        articleViewModel = ViewModelProviders.of(this).get(ArticleViewModel.class);
        articleViewModel.getUrl().observe(this, (String s) -> articleViewModel.fetchFeed(s));
        recyclerViewRss.setLayoutManager(new LinearLayoutManager(root.getContext()));
        recyclerViewRss.setItemAnimator(new DefaultItemAnimator());
        recyclerViewRss.setHasFixedSize(true);
        articleViewModel.getArticleList().observe(this, (List<Article> articles) -> {
            if (articles != null) {
                articleAdapter = new ArticleAdapter(articles, root.getContext());
                recyclerViewRss.setAdapter(articleAdapter);
                articleAdapter.notifyDataSetChanged();
                progressBarRss.setVisibility(View.GONE);
                swipeRefreshLayoutRss.setRefreshing(false);
            }
        });
        articleViewModel.getSnackBar().observe(this, (String s) -> {
            if (s != null) {
                Snackbar.make(constraintLayoutRss, s, Snackbar.LENGTH_LONG).show();
                articleViewModel.onSnackBarShowed();
            }
        });
        swipeRefreshLayoutRss.setColorSchemeResources(R.color.colorAccent, R.color.colorPrimaryDark);
        swipeRefreshLayoutRss.canChildScrollUp();
        swipeRefreshLayoutRss.setOnRefreshListener(() -> {
            articleAdapter.getArticleList().clear();
            articleAdapter.notifyDataSetChanged();
            swipeRefreshLayoutRss.setRefreshing(true);
            articleViewModel.getUrl().observe(this, (String s) -> articleViewModel.fetchFeed(s));
        });
        return root;
    }
}
