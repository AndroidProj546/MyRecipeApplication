package com.example.myrecipe;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;
public class RecipeAdapter extends RecyclerView.Adapter<RecipeAdapter.ViewHolder> {
    private List<Recipe> recipeList;
    private OnItemClickListener listener;
    private Context context;
    public interface OnItemClickListener {
        void onItemClick(Recipe recipe);
        void onEditClick(Recipe recipe);
        void onFavClick(Recipe recipe);
    }

    // Constructor
    public RecipeAdapter(List<Recipe> recipeList, OnItemClickListener listener, Context context) {
        this.recipeList = recipeList;
        this.listener = listener;
        this.context = context;
    }

    // ViewHolder class
    public class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView textViewTitle;
        private final TextView textViewCategory;
        private Button edit;
        private Button viewRecipe;
        private ImageView recipeImage;
        private ImageView fav;

        public ViewHolder(View view) {
            super(view);
            textViewTitle = view.findViewById(R.id.title);
            textViewCategory = view.findViewById(R.id.category);
            recipeImage = view.findViewById(R.id.recipeImage);
            edit = view.findViewById(R.id.edit);
            viewRecipe = view.findViewById(R.id.view);
            fav = view.findViewById(R.id.fav);
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

            // Apply the color filter
            String hexColorCode = recipe.isFavourite() ? "#FFD700" : "#808080";
            int color = Color.parseColor(hexColorCode);
            Drawable drawable = fav.getDrawable();
            drawable.setColorFilter(color, PorterDuff.Mode.SRC_IN);

            // Update the ImageView with the modified drawable
            fav.setImageDrawable(drawable);

            fav.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        listener.onFavClick(recipeList.get(position));
                    }
                }
            });
            edit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        listener.onEditClick(recipeList.get(position));
                    }
                }
            });

            viewRecipe.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        listener.onItemClick(recipeList.get(position));
                    }
                }
            });
        }
    }

    @Override
    public RecipeAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recipe, parent, false);
        return new ViewHolder(view);
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
