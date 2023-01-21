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

public class SourcesDownloader {

    //////////////////////////////////////////////////////////////////////////////////

    //old
    private static final String yourAPIKey = "4eb61da84756466e8b0cb1276d05167a";
    //private static final String yourAPIKey = "21b67e4c6a364eaabd7a6bda31f1b1b1";
    private static final String sourcesURL = "https://newsapi.org/v2/sources";
    private static final String TAG = "SourcesDownloader";
    //
    //////////////////////////////////////////////////////////////////////////////////


    public static void performDownload(MainActivity mainActivity) {

        RequestQueue queue = Volley.newRequestQueue(mainActivity);;

        Uri.Builder buildURL = Uri.parse(sourcesURL).buildUpon();

        String urlToUse = buildURL.build().toString();




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
        // Request a string response from the provided URL.
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
//        Log.d(TAG, "onResponse: " + response.toString());
        ArrayList<Source> sourceArrayList = new ArrayList<>();
        try {

            if(response.has("sources")) {
                JSONArray sourcesArray = (JSONArray) response.getJSONArray("sources");
                for(int i = 0; i< sourcesArray.length();i++ ){
                    JSONObject source = sourcesArray.getJSONObject(i);
//                    Log.d(TAG, "handleResults: " + source);
                    String id = null;
                    if(source.has("id")){
                        id = source.getString("id");
                    }
                    //Log.d(TAG, "handleResults: ID: " + id);

                    String name = null;
                    if(source.has("name")){
                        name = source.getString("name");
                    }
                    //Log.d(TAG, "handleResults: NAME: " + name);

                    String category = null;
                    if(source.has("category")){
                        category = source.getString("category");
                    }
                    //Log.d(TAG, "handleResults: CATEGORY: " + category);

                    String language = null;
                    if(source.has("language")){
                        language = source.getString("language");
                    }
                    //Log.d(TAG, "handleResults: LANGUAGE: " + language);

                    String country = null;
                    if(source.has("country")){
                        country = source.getString("country");
                    }
                    //Log.d(TAG, "handleResults: COUNTRY: " + country);
                    //Log.d(TAG, "handleResults: " + "---------------------");
                    sourceArrayList.add(new Source(id, name, category, language, country));

                }
                mainActivity.updateSourceArrayList(sourceArrayList);
//                for (Source source: sourceArrayList) {
//                    Log.d(TAG, "handleResults: " + source.toString() );
//
//                }
//                Log.d(TAG, "handleResults: " + sourcesArray);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
