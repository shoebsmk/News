package com.example.news;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

public class ArticleAdapter extends RecyclerView.Adapter<ArticleViewHolder> {

    private final SimpleDateFormat sdf =
            new SimpleDateFormat("MMM d, yyyy h:mm", Locale.getDefault());
    private final MainActivity mainActivity;
    private final ArrayList<Article> articleList;
    private static final String TAG = "ArticleAdapter";

    public ArticleAdapter(MainActivity mainActivity, ArrayList<Article> articleList) {
        this.mainActivity = mainActivity;
        this.articleList = articleList;
    }

    @NonNull
    @Override
    public ArticleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ArticleViewHolder(
                LayoutInflater.from(parent.getContext()).
                        inflate(R.layout.news_entry, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ArticleViewHolder holder, int position) {
        Article article = articleList.get(position);

        holder.author.setText(article.getAuthor());
        holder.title.setText(article.getTitle());

//        String extraText = null;
//        for (int i =0 ; i <20 ; i++) extraText += article.getDescription();
        holder.description.setText(article.getDescription());

        holder.publishedAt.setText(article.getPublishedAt());
        holder.articleCount.setText(String.format(Locale.getDefault(), "%d of %d", position +1, articleList.size()));
        holder.author.setOnClickListener(v -> clickArticleLink(article.getUrl()));
        holder.title.setOnClickListener(v -> clickArticleLink(article.getUrl()));
        holder.urlToImageIV.setOnClickListener(v -> clickArticleLink(article.getUrl()));
        holder.description.setOnClickListener(v -> clickArticleLink(article.getUrl()));

        if (article.getUrlToImage() != null) {

            Glide.with(mainActivity)
                    .load(article.getUrlToImage())
                    //.load("https://cdn.britannica.com/33/194733-050-4CF75F31/Girl-with-a-Pearl-Earring-canvas-Johannes-1665.jpg")
                    .addListener(new RequestListener<Drawable>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, com.bumptech.glide.load.DataSource dataSource, boolean isFirstResource) {
                            return false;
                        }

                    })
                    .error(R.drawable.brokenimage)
                    .placeholder(R.drawable.loading)
                    .into(holder.urlToImageIV);


        } else {
            holder.urlToImageIV.setImageResource(R.drawable.noimage);
        }
    }

    @Override
    public int getItemCount() {
        return articleList.size();
    }

    private void clickArticleLink(String url) {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        // Check if there is an app that can handle https intents
        if (intent.resolveActivity(mainActivity.getPackageManager()) != null) {
            mainActivity.startActivity(intent);
        } else {
            Log.d(TAG, "clickArticleLink: No App to open link / broken url");
        }
    }

}
