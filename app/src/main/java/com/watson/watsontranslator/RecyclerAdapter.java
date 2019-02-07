package com.watson.watsontranslator;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class RecyclerAdapter extends RecyclerView.Adapter {

    private List<String> historyTextList = new ArrayList<>();
    private List<String> historyLanguageList = new ArrayList<>();

    RecyclerAdapter(List<String> historyTextList, List<String> historyLanguageList) {
        if (historyTextList.size() != 0) {
            this.historyTextList = historyTextList;
            this.historyLanguageList = historyLanguageList;
        } else {

            /*Case with empty history*/
            this.historyTextList.add("Вы ещё ничего не искали");
            this.historyLanguageList.add("Russian");
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item, parent, false);
        return new RecyclerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ((RecyclerViewHolder) holder).bindView(position);
    }

    @Override
    public int getItemCount() {
        if (historyTextList == null)
            return 1;
        return historyTextList.size();
    }

    private class RecyclerViewHolder extends RecyclerView.ViewHolder {

        private TextView tvHistoryText, tvHistoryLanguage;

        RecyclerViewHolder(View itemView) {
            super(itemView);

            tvHistoryText = itemView.findViewById(R.id.tvHistoryText);
            tvHistoryLanguage = itemView.findViewById(R.id.tvHistoryLanguage);
        }

        void bindView(int position) {

            tvHistoryText.setText(String.format("Фраза : %s", historyTextList.get(position)));
            tvHistoryLanguage.setText(String.format("Язык : %s", historyLanguageList.get(position)));

        }
    }
}
