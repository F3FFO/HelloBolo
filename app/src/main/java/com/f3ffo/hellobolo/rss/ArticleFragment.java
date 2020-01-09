package com.f3ffo.hellobolo.rss;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.f3ffo.hellobolo.R;
import com.google.android.material.snackbar.Snackbar;
import com.prof.rssparser.Article;

import java.util.List;

public class ArticleFragment extends Fragment {

    private static String ARG_SECTION_NUMBER = "";
    private ArticleViewModel articleViewModel;
    private ProgressBar progressBarRss;
    private SwipeRefreshLayout swipeRefreshLayoutRss;
    private ArticleAdapter articleAdapter;
    private RecyclerView recyclerViewRss;
    private ConstraintLayout constraintLayoutRss;

    static ArticleFragment newInstance(int index) {
        ArticleFragment fragment = new ArticleFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(ARG_SECTION_NUMBER, index);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        articleViewModel = new ViewModelProvider(ArticleFragment.this).get(ArticleViewModel.class);
        int index = 1;
        if (getArguments() != null) {
            index = getArguments().getInt(ARG_SECTION_NUMBER);
        }
        articleViewModel.setIndex(index);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_rss, container, false);
        constraintLayoutRss = view.findViewById(R.id.constraintLayoutRss);
        progressBarRss = view.findViewById(R.id.progressBarRss);
        recyclerViewRss = view.findViewById(R.id.recyclerViewRss);
        swipeRefreshLayoutRss = view.findViewById(R.id.swipeRefreshLayoutRss);
        articleViewModel = new ViewModelProvider(ArticleFragment.this).get(ArticleViewModel.class);
        articleViewModel.getUrl().observe(ArticleFragment.this.getViewLifecycleOwner(), (String s) -> articleViewModel.fetchFeed(inflater.getContext(), s));
        recyclerViewRss.setLayoutManager(new LinearLayoutManager(view.getContext()));
        recyclerViewRss.setItemAnimator(new DefaultItemAnimator());
        recyclerViewRss.setHasFixedSize(true);
        articleViewModel.getArticleList().observe(ArticleFragment.this.getViewLifecycleOwner(), (List<Article> articles) -> {
            if (articles != null) {
                articleAdapter = new ArticleAdapter(articles, view.getContext());
                recyclerViewRss.setAdapter(articleAdapter);
                articleAdapter.notifyDataSetChanged();
                progressBarRss.setVisibility(View.GONE);
                swipeRefreshLayoutRss.setRefreshing(false);
            }
        });
        articleViewModel.getSnackBar().observe(ArticleFragment.this.getViewLifecycleOwner(), (String s) -> {
            if (s != null) {
                Snackbar.make(constraintLayoutRss, s, Snackbar.LENGTH_LONG).show();
                articleViewModel.onSnackBarShowed();
            }
        });
        swipeRefreshLayoutRss.setProgressBackgroundColorSchemeResource(R.color.colorPrimary);
        swipeRefreshLayoutRss.setColorSchemeResources(R.color.colorAccent);
        swipeRefreshLayoutRss.canChildScrollUp();
        swipeRefreshLayoutRss.setOnRefreshListener(() -> {
            articleAdapter.getArticleList().clear();
            articleAdapter.notifyDataSetChanged();
            swipeRefreshLayoutRss.setRefreshing(true);
            articleViewModel.getUrl().observe(ArticleFragment.this.getViewLifecycleOwner(), (String s) -> articleViewModel.fetchFeed(inflater.getContext(), s));
        });
        return view;
    }
}