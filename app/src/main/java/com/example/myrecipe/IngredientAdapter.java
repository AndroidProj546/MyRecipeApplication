package com.example.myrecipe;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

public class IngredientAdapter extends RecyclerView.Adapter<IngredientAdapter.ViewHolder> {
    private List<String> ingredients;

    // Constructor
    public IngredientAdapter(List<String> ingredients) {
        this.ingredients = ingredients;
    }

    // ViewHolder class
    public class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView textViewIngredient;
        public ViewHolder(View view) {
            super(view);
            textViewIngredient = view.findViewById(R.id.ingredientText);
        }

        public void bind(String ingredient) {
            textViewIngredient.setText(ingredient);
        }
    }

    @Override
    public IngredientAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.ingredient, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        String ingredient = ingredients.get(position);
        holder.bind(ingredient);
    }

    @Override
    public int getItemCount() {
        return ingredients.size();
    }

}
