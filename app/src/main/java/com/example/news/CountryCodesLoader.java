package com.example.news;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class CountryCodesLoader implements Runnable {

    private final MainActivity mainActivity;

    public CountryCodesLoader(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
    }

    @Override
    public void run() {
        try {
            InputStream is =
                    mainActivity.getResources().openRawResource(R.raw.country_codes);
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));

            StringBuilder result = new StringBuilder();
            for (String line; (line = reader.readLine()) != null; ) {
                result.append(line);
            }

            ArrayList<CountryCode> countryCodeArrayList = new ArrayList<>();
            JSONObject jsonObjectM = new JSONObject(result.toString());
            JSONArray jsonArray =  jsonObjectM.getJSONArray("countries");
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                String code = jsonObject.getString("code");
                String name = jsonObject.getString("name");

                CountryCode countryCode = new CountryCode(code, name);
                countryCodeArrayList.add(countryCode);
            }

            mainActivity.runOnUiThread(() ->
                    mainActivity.acceptResults(countryCodeArrayList));
        } catch (Exception e) {
            mainActivity.runOnUiThread(() ->
                    mainActivity.acceptResults(null));
        }
    }
}
