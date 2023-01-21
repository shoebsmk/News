package com.example.news;

import android.net.Uri;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ArticlesDownloader {

    //old
    private static final String yourAPIKey = "4eb61da84756466e8b0cb1276d05167a";
    //private static final String yourAPIKey = "21b67e4c6a364eaabd7a6bda31f1b1b1";
    private static final String sourcesURL = "https://newsapi.org/v2/top-headlines?sources=";
    private static final String TAG = "ArticlesDownloader";

    public static void performDownload(MainActivity mainActivity, String source) {

        RequestQueue queue = Volley.newRequestQueue(mainActivity);

        Uri.Builder buildURL = Uri.parse(sourcesURL).buildUpon();

        String urlToUse = buildURL.build().toString();

        urlToUse += source;

        Response.Listener<JSONObject> listener =
                response -> handleResults(mainActivity, response);

        Response.ErrorListener error = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                try {
                    JSONObject jsonObject = new JSONObject(new String(error.networkResponse.data));
                    Log.d(TAG, "onErrorResponse: " + MessageFormat.format("Error: {0}", jsonObject.toString()));
                    //setTitle("Duration: " + (System.currentTimeMillis() - start));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };

        JsonObjectRequest jsonObjectRequest =
                new JsonObjectRequest(Request.Method.GET, urlToUse,
                        null, listener, error) {
                    @Override
                    public Map<String, String> getHeaders() throws AuthFailureError {
                        Map<String, String> headers = new HashMap<>();
                        headers.put("User-Agent", "News-App");
                        headers.put("X-Api-Key", yourAPIKey);
                        return headers;
                    }
                };
        // Add the request to the RequestQueue.
        queue.add(jsonObjectRequest);
    }

    private static void handleResults(MainActivity mainActivity, JSONObject response) {
        ArrayList<Article> articlesArrayList = new ArrayList<>();
        try {
            JSONArray articlesArray = (JSONArray) response.getJSONArray("articles");
            //Log.d(TAG, "handleResults: " + articlesArray);

            for(int i = 0; i< articlesArray.length();i++ ) {
                JSONObject source = articlesArray.getJSONObject(i);
//                    Log.d(TAG, "handleResults: " + source);
                String author = null;
                if (source.has("author")) {
                    author = source.getString("author");
                }
                //Log.d(TAG, "handleResults: AUTHOR: " + author);

                String title = null;
                if (source.has("title")) {
                    title = source.getString("title");
                }
                //Log.d(TAG, "handleResults: TITLE: " + title);

                String description = null;
                if (source.has("description")) {
                    description = source.getString("description");
                }
                //Log.d(TAG, "handleResults: DESCRIPTION: " + description);

                String url = null;
                if (source.has("url")) {
                    url = source.getString("url");
                }
                //Log.d(TAG, "handleResults: URL: " + url);

                String urlToImage = null;
                if (source.has("urlToImage")) {
                    urlToImage = source.getString("urlToImage");
                }
                //Log.d(TAG, "handleResults: URLTOIMAGE: " + urlToImage);

                String publishedAt = null;
                if (source.has("publishedAt")) {
                    publishedAt = source.getString("publishedAt");
                }
                //Log.d(TAG, "handleResults: PUBLISHEDAT: " + publishedAt);

                //Log.d(TAG, "handleResults: ----------------------" );

                articlesArrayList.add(new Article(author, title, description, url, urlToImage, publishedAt));
            }
            mainActivity.updateArticleArrayList(articlesArrayList);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
