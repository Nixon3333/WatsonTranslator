package com.watson.watsontranslator;

import android.content.DialogInterface;
import android.content.Loader;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;

public class TextFragment extends Fragment {


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        getActivity().setTitle("Новый текст");

        FloatingActionButton fab = (FloatingActionButton) view.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {

                EditText editText = view.getRootView().findViewById(R.id.etForRequest);
                String text = String.valueOf(editText.getText());

                new Task(new CallBack() {
                    @Override
                    public void onComplite(String result) {
                        View view = Objects.requireNonNull(getView()).getRootView();
                        makeDialog(view, result);
                        Log.d("onComplite", "atTask");
                    }
                }).execute(text);
            }
        });
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        return inflater.inflate(R.layout.text_fragment, container, false);
    }


    public class Task extends AsyncTask<String, Void, String> {

        private CallBack callback;
        private FrameLayout progressLayout = getView().getRootView().findViewById(R.id.progressLayout);

        public Task(CallBack callback) {
            this.callback = callback;
        }

        @Override
        protected void onPreExecute() {

            progressLayout.setVisibility(View.VISIBLE);
        }

        @Override
        protected String doInBackground(String... params) {

            final String text = params[0];
            final String[] result = {null};

            MediaType JSON = MediaType.parse("text/plain; charset=utf-8");

            RequestBody body = RequestBody.create(JSON, text);

            Request request = new Request.Builder()
                    .header("Authorization", "Basic MWFmMWJiYTMtM2VjZS00ZmQ5LTg0YzItYTQwODI4MmM3Y2ZlOkJINWVhM0ZCSHBXRg==")
                    .url("https://gateway.watsonplatform.net/language-translator/api/v3/identify?version=2018-05-01")
                    .method("POST", body)
                    .build();

            OkHttpClient client = new OkHttpClient();
            ResponseBody responseBody = null;

            try {
                okhttp3.Response response = client.newCall(request).execute();
                responseBody = response.body();
                System.out.println("MyResp : " + response);
                String s = responseBody.string().trim();
                System.out.println("MyRespBody : " + s);
                result[0] = parseJsonResponse(s);
                Log.d("result[0]", result[0]);
            } catch (IOException e) {
                e.printStackTrace();
            }


            return result[0];
        }


        @Override
        protected void onPostExecute(String s) {
            if (callback != null) callback.onComplite(s);
            progressLayout.setVisibility(View.GONE);

        }
    }


    private String parseJsonResponse(String string) {
        String lang = null;
        try {
            JSONObject jsonObject = new JSONObject(string);
            JSONArray jsonArray = jsonObject.getJSONArray("languages");
            JSONObject jsonLang = jsonArray.getJSONObject(0);
            lang = jsonLang.getString("language");
            System.out.println("jsonObject.toString : " + jsonObject.toString());
            System.out.println("lang : " + lang);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        //identifyLanguage(lang);
        return identifyLanguage(lang);
    }

    private String identifyLanguage(String s) {
        try {
            JSONObject obj = new JSONObject(readJSONFromAsset());
            JSONArray jsonArray = obj.getJSONArray("languages");
            Log.d("JSON", obj.toString());
            Log.d("JSON", jsonArray.getString(1));
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                if (jsonObject.get("language").equals(s))
                    s = String.valueOf(jsonObject.get("name"));
            }

        } catch (JSONException e) {
            e.printStackTrace();
            Log.d("JSONErr", e.getMessage());
        }

        return s;
    }


    public String readJSONFromAsset() {
        String json = null;
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

    protected static void makeDialog(View v, String string) {
        View view = v.getRootView();
        AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
        builder.setTitle("Язык определён")
                .setMessage("Ваша фраза написана на " + string)
                .setCancelable(false)
                .setNegativeButton("Оk",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });
        AlertDialog alert = builder.create();
        alert.show();
    }

}
