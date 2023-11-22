package com.example.myrecipe;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

public class FavRecipeAdapter extends RecyclerView.Adapter<FavRecipeAdapter.ViewHolder> {
    private List<Recipe> recipeList;
    private OnItemClickListener listener;
    private Context context;
    public interface OnItemClickListener {
        void onItemClick(Recipe recipe);

    }

    // Constructor
    public FavRecipeAdapter(List<Recipe> recipeList, OnItemClickListener listener, Context context) {
        this.recipeList = recipeList;
        this.listener = listener;
        this.context = context;
    }

    // ViewHolder class
    public class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView textViewTitle;
        private final TextView textViewCategory;
        private ImageView recipeImage;

        public ViewHolder(View view, final OnItemClickListener listener) {
            super(view);
            textViewTitle = view.findViewById(R.id.title);
            textViewCategory = view.findViewById(R.id.category);
            recipeImage = view.findViewById(R.id.recipeImage);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        listener.onItemClick(recipeList.get(position));
                    }
                }
            });
        }

        public void bind(Recipe recipe) {
            textViewTitle.setText(recipe.getTitle());
            textViewCategory.setText(recipe.getCategory());
            String imagePath = recipe.getImagePath();
            if (imagePath != null && !imagePath.isEmpty()) {
                Glide.with(context)
                        .load("file://" + imagePath)
                        .into(recipeImage);
            }
        }
    }

    @Override
    public FavRecipeAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recipe2, parent, false);
        return new ViewHolder(view, listener);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Recipe recipe = recipeList.get(position);
        holder.bind(recipe);
    }

    @Override
    public int getItemCount() {
        return recipeList.size();
    }

    public void setRecipeList(List<Recipe> recipeList) {
        this.recipeList = recipeList;
        notifyDataSetChanged();
    }
}
