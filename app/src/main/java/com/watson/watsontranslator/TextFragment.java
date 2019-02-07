package com.watson.watsontranslator;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class TextFragment extends Fragment implements LoaderManager.LoaderCallbacks<String> {

    private final String TAG = getClass().getSimpleName();
    protected static List<String> textList = new ArrayList<>();
    protected static List<String> langList = new ArrayList<>();

    @SuppressLint("StaticFieldLeak")
    protected static View view;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        TextFragment.view = view;

        Log.d(TAG + "Size", String.valueOf(textList.size()));

        Objects.requireNonNull(getActivity()).setTitle(R.string.text_fragment_title);

        FloatingActionButton fab = view.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {

                EditText editText = view.getRootView().findViewById(R.id.etForRequest);
                String text = String.valueOf(editText.getText());
                if(!text.trim().isEmpty()) {
                    Bundle bundle = new Bundle();
                    bundle.putString("text", text);
                    textList.add(0, text);

                    getActivity().getSupportLoaderManager().initLoader(textList.size(), bundle, TextFragment.this).forceLoad();
                } else {
                    Toast.makeText(getContext(), R.string.empty_edittext, Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        return inflater.inflate(R.layout.text_fragment, container, false);
    }


    protected static void makeDialog(View v, String string) {
        View view = v.getRootView();
        AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
        if (string.equals("error")) {
            TextFragment.textList.remove(0);
            builder.setTitle(R.string.error_dialog_title)
                    .setMessage("Сервер не отвечает или отсутствует интернет-соединение")

                    .setCancelable(false)
                    .setNegativeButton("Оk",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                }
                            });
            AlertDialog alert = builder.create();
            alert.show();
        } else {
            builder.setTitle(R.string.success_identify_title)
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


    @Override
    public Loader<String> onCreateLoader(int id, Bundle args) {

        FrameLayout progressLayout = Objects.requireNonNull(getView()).getRootView().findViewById(R.id.progressLayout);
        progressLayout.setVisibility(View.VISIBLE);
        return new IdentifyTask(getContext(), Objects.requireNonNull(args.get("text")).toString());
    }

    @Override
    public void onLoadFinished(Loader<String> loader, String data) {
        if (data != null)
            Log.d(TAG + " LoaderFinish_data : ", data);
        FrameLayout progressLayout = Objects.requireNonNull(getView()).getRootView().findViewById(R.id.progressLayout);
        progressLayout.setVisibility(View.GONE);
    }

    @Override
    public void onLoaderReset(Loader<String> loader) {

    }


    public static void showDialog(String s) {
        makeDialog(TextFragment.view, s);
    }
}
