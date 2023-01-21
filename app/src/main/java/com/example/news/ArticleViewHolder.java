package com.example.news;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class ArticleViewHolder extends RecyclerView.ViewHolder {

    TextView author;
    TextView title;
    TextView description;
    ImageView urlToImageIV;
    TextView publishedAt;
    TextView articleCount;

    public ArticleViewHolder(@NonNull View itemView) {
        super(itemView);
        author = itemView.findViewById(R.id.authorTV);
        title = itemView.findViewById(R.id.titleTV);
        description = itemView.findViewById(R.id.descriptionTV);
        urlToImageIV = itemView.findViewById(R.id.urlToImageIV);
        publishedAt = itemView.findViewById(R.id.publishedAtTV);
        articleCount = itemView.findViewById(R.id.articleCountTV);
    }
}
