package com.f3ffo.hellobolo.rss;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import com.f3ffo.hellobolo.R;
import com.prof.rssparser.Article;
import com.prof.rssparser.OnTaskCompleted;
import com.prof.rssparser.Parser;

import java.util.ArrayList;
import java.util.List;

public class ArticleViewModel extends ViewModel {

    private MutableLiveData<List<Article>> articleListLive = null;
    private MutableLiveData<String> snackBar = new MutableLiveData<>();
    private MutableLiveData<Integer> mIndex = new MutableLiveData<>();
    private LiveData<String> url = Transformations.map(mIndex, (Integer input) -> {
        if (input == 1) {
            return "https://www.tper.it/rss.xml";
        } else if (input == 2) {
            return "https://www.tper.it/tutte-le-news/rss.xml";
        } else {
            return "https://www.tper.it/taxonomy/term/33/all/rss.xml";
        }
    });

    public void setIndex(int index) {
        mIndex.setValue(index);
    }

    public MutableLiveData<List<Article>> getArticleList() {
        if (articleListLive == null) {
            articleListLive = new MutableLiveData<>();
        }
        return articleListLive;
    }

    private void setArticleList(List<Article> articleList) {
        this.articleListLive.postValue(articleList);
    }

    public LiveData<String> getUrl() {
        return url;
    }

    public LiveData<String> getSnackBar() {
        return snackBar;
    }

    public void onSnackBarShowed() {
        snackBar.setValue(null);
    }

    public void fetchFeed(Context context, String urlString) {
        Parser parser = new Parser();
        parser.onFinish(new OnTaskCompleted() {

            @Override
            public void onTaskCompleted(List<Article> list) {
                setArticleList(list);
            }

            @Override
            public void onError(Exception e) {
                setArticleList(new ArrayList<>());
                e.printStackTrace();
                snackBar.postValue(context.getString(R.string.rss_error_load));
            }
        });
        parser.execute(urlString);
    }
}