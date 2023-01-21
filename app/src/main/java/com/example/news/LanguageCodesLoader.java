package com.example.news;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class LanguageCodesLoader implements Runnable {

    private final MainActivity mainActivity;

    public LanguageCodesLoader(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
    }

    @Override
    public void run() {
        try {
            InputStream is =
                    mainActivity.getResources().openRawResource(R.raw.language_codes);
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));

            StringBuilder result = new StringBuilder();
            for (String line; (line = reader.readLine()) != null; ) {
                result.append(line);
            }

            ArrayList<LanguageCode> languageCodeArrayList = new ArrayList<>();
            JSONObject jsonObjectM = new JSONObject(result.toString());
            JSONArray jsonArray =  jsonObjectM.getJSONArray("languages");
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                String code = jsonObject.getString("code");
                String name = jsonObject.getString("name");

                LanguageCode languageCode = new LanguageCode(code, name);
                languageCodeArrayList.add(languageCode);
            }

            mainActivity.runOnUiThread(() ->
                    mainActivity.acceptResultsLCD(languageCodeArrayList));
        } catch (Exception e) {
            mainActivity.runOnUiThread(() ->
                    mainActivity.acceptResultsLCD(null));
        }
    }
}
