package com.girgin.wordest.Adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.girgin.wordest.Activities.ActivityMain;
import com.girgin.wordest.App;
import com.girgin.wordest.R;
import com.girgin.wordest.SQLite.SQLite;

import java.util.ArrayList;

public class AdapterActivityMainWordsList extends RecyclerView.Adapter<AdapterActivityMainWordsList.AdapterWord> {

    private final Context context;
    private final ArrayList<ArrayList<String>> list;

    public AdapterActivityMainWordsList(Context context, ArrayList<ArrayList<String>> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public AdapterWord onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_activity_main_recyclerview_words, parent, false);
        return new AdapterWord(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterWord holder, @SuppressLint("RecyclerView") int position) {
        //WRITE WORD TO TEXT
        holder.textView.setText(list.get(position).get(1).toLowerCase());
        holder.textView.setOnClickListener(v -> {
            if (!list.get(position).get(0).equals("0")) {
                ((ActivityMain) context).loadFragmentWordInfo(Integer.parseInt(list.get(position).get(0)), list.get(position).get(1));
            } else {
                ((ActivityMain) context).loadFragmentAddWord();
            }
        });
        //SPEECH WORD
        holder.imageButtonSpeech.setOnClickListener(v -> App.speechEnglish(v.getContext(), list.get(position).get(1)));
        //SET FAVORITE ICON WHEN LOADED
        loadFavoriteImageButtons(holder, position);
        //SET FAVORITE ICON WHEN CHANGED
        holder.imageButtonFavorite.setOnClickListener(v -> changeFavoriteStatement(holder, position));
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class AdapterWord extends RecyclerView.ViewHolder {

        TextView textView;
        ImageButton imageButtonFavorite, imageButtonSpeech;

        public AdapterWord(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.activity_main_recyclerview_item_textview);
            imageButtonFavorite = itemView.findViewById(R.id.activity_main_recyclerview_item_imageButton_favorite);
            imageButtonSpeech = itemView.findViewById(R.id.activity_main_recyclerview_item_imageButton_speech);
        }
    }

    void loadFavoriteImageButtons(AdapterWord holder, int position) {
        if (list.get(position).get(2).equals("1")) {
            holder.imageButtonFavorite.setImageResource(R.drawable.icon_favorite_filled);
        } else {
            holder.imageButtonFavorite.setImageResource(R.drawable.icon_favorite_empty);
        }
        if (list.get(position).get(0).equals("0")) {
            holder.imageButtonFavorite.setVisibility(View.INVISIBLE);
            holder.imageButtonSpeech.setVisibility(View.INVISIBLE);
        }
    }

    void changeFavoriteStatement(AdapterWord holder, int position) {
        SQLite sqLite = new SQLite(context);
        if (list.get(position).get(2).equals("1")) {
            sqLite.changeFavoriteState(Integer.parseInt(list.get(position).get(0)));
            list.get(position).set(2, "0");
            holder.imageButtonFavorite.setImageResource(R.drawable.icon_favorite_empty);
        } else {
            sqLite.changeFavoriteState(Integer.parseInt(list.get(position).get(0)));
            list.get(position).set(2, "1");
            holder.imageButtonFavorite.setImageResource(R.drawable.icon_favorite_filled);
        }
    }
}
