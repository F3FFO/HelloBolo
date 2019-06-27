package com.f3ffo.hellobusbologna.rss;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.prof.rssparser.Article;
import com.prof.rssparser.OnTaskCompleted;
import com.prof.rssparser.Parser;

import java.util.ArrayList;
import java.util.List;

public class ArticleItem extends ViewModel {

    private MutableLiveData<List<Article>> articleListLive = null;
    private String urlString = "https://www.tper.it/tutte-le-news/rss.xml";
    private MutableLiveData<String> snackBar = new MutableLiveData<>();

    public MutableLiveData<List<Article>> getArticleList() {
        if (articleListLive == null) {
            articleListLive = new MutableLiveData<>();
        }
        return articleListLive;
    }

    private void setArticleList(List<Article> articleList) {
        this.articleListLive.postValue(articleList);
    }

    public String getUrlString() {
        return urlString;
    }

    public void setUrlString(String urlString) {
        this.urlString = urlString;
    }

    public LiveData<String> getSnackBar() {
        return snackBar;
    }

    public void onSnackBarShowed() {
        snackBar.setValue(null);
    }

    public void fetchFeed() {
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
                snackBar.postValue("C'è stato un errore. Per favore riprova");
            }
        });
        parser.execute(urlString);
    }
}
