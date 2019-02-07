package com.watson.watsontranslator;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v4.content.AsyncTaskLoader;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;


public class IdentifyTask extends AsyncTaskLoader<String> {

    private final String TAG = getClass().getSimpleName();
    private String text;
    static String result;

    IdentifyTask(Context context, String text) {
        super(context);
        this.text = text;
    }

    @Override
    protected void onStartLoading() {
        Log.d(TAG, "start");
        if (result == null) {
            forceLoad();
        } else {
            if (!result.equals("afterDialog")) {
                deliverResult(result);
                result = null;
            }
        }
    }

    @Override
    public String loadInBackground() {


        String result = null;

        MediaType JSON = MediaType.parse("text/plain; charset=utf-8");

        RequestBody body = RequestBody.create(JSON, text);

        Request request = new Request.Builder()
                .header("Authorization", "Basic MWFmMWJiYTMtM2VjZS00ZmQ5LTg0YzItYTQwODI4MmM3Y2ZlOkJINWVhM0ZCSHBXRg==")
                .url("https://gateway.watsonplatform.net/language-translator/api/v3/identify?version=2018-05-01")
                .method("POST", body)
                .build();

        OkHttpClient client = new OkHttpClient();
        ResponseBody responseBody;

        try {
            okhttp3.Response response = client.newCall(request).execute();
            Log.d(TAG + " response : ", String.valueOf(response));
            responseBody = response.body();
            String s = responseBody.string();
            Log.d(TAG + "responseBody : ", s);
            result = parseJsonResponse(s);
            Log.d(TAG + " result : ", result);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;

    }

    @Override
    public void deliverResult(@Nullable String data) {
        if (data == null) {
            TextFragment.showDialog("error");
            result = "ERROR";
        } else {
            result = data;
            TextFragment.showDialog(data);
            TextFragment.langList.add(0, data);
            Log.d(TAG + " data : ", data);
        }

        super.deliverResult(data);

    }

    private String parseJsonResponse(String string) {
        String lang = null;
        try {
            JSONObject jsonObject = new JSONObject(string);
            Log.d(TAG + " jsonObject.toString : ", jsonObject.toString());
            JSONArray jsonArray = jsonObject.getJSONArray("languages");
            JSONObject jsonLang = jsonArray.getJSONObject(0);
            lang = jsonLang.getString("language");
            Log.d(TAG + " lang : ", lang);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return identifyLanguage(lang);
    }

    /*Identify language from response*/
    private String identifyLanguage(String s) {
        try {
            JSONObject obj = new JSONObject(readJSONFromAsset());
            Log.d(TAG + " JSONobj :", obj.toString());
            JSONArray jsonArray = obj.getJSONArray("languages");
            Log.d(TAG + " jsonArray ", jsonArray.getString(1));
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                if (jsonObject.get("language").equals(s))
                    s = String.valueOf(jsonObject.get("name"));
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return s;
    }

    /*Looking for language full name*/
    private String readJSONFromAsset() {
        String json;
        try {
            InputStream is = getContext().getAssets().open("languages.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;
    }

}

