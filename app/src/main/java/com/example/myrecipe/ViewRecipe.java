package com.example.myrecipe;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.ArrayList;

public class ViewRecipe extends AppCompatActivity {
    private Toolbar toolbar;
    private Recipe recipe;
    private DatabaseHelper db;
    private TextView titleText;
    private TextView categoryText;
    private TextView instructionsText;
    private RecyclerView ingredientsList;
    private ImageView edit;
    private ImageView delete;
    private ImageView fav, recipeImage;
    private IngredientAdapter ingredientAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_recipe);

        // initialize views
        toolbar = findViewById(R.id.toolbar);
        titleText = findViewById(R.id.title);
        categoryText = findViewById(R.id.category);
        instructionsText = findViewById(R.id.instructions);
        ingredientsList = findViewById(R.id.ingredientsList);
        edit = findViewById(R.id.edit);
        delete = findViewById(R.id.delete);
        fav = findViewById(R.id.fav);
        recipeImage = findViewById(R.id.recipeImage);

        // initialize db
        db = new DatabaseHelper(this);

        setSupportActionBar(toolbar);
        // Enable the back button
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        int recipeId = getIntent().getIntExtra("RecipeID", -1);
        if (recipeId != -1) {
            // Load recipe details for editing
            recipe = db.getRecipeById(recipeId);
            titleText.setText(recipe.getTitle());
            categoryText.setText(recipe.getCategory());
            instructionsText.setText(recipe.getInstructions());
            ArrayList<String> ingredients = new ArrayList<>();
            for (String ingredient: recipe.getIngredients().replaceAll("\n", "").split(","))
                ingredients.add(ingredient);

            Log.d("Planify", "Ingredients " + ingredients.size());
            ingredientAdapter = new IngredientAdapter(ingredients);
            ingredientsList.setAdapter(ingredientAdapter);
            ingredientsList.setLayoutManager(new LinearLayoutManager(this));
            Glide.with(this)
                    .load("file://" + recipe.getImagePath())
                    .into(recipeImage);

        }

        // handle events
        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (recipeId != -1){
                    new MaterialAlertDialogBuilder(ViewRecipe.this)
                            .setTitle("Edit recipe")
                            .setMessage("Do you want to edit the recipe: " + recipe.getTitle())
                            .setPositiveButton("Edit", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Intent intent = new Intent(ViewRecipe.this, AddRecipe.class);
                                    intent.putExtra("RecipeID", recipeId);
                                    startActivity(intent);
                                }
                            })
                            .setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.cancel();
                                }
                            }).show();
                }
            }
        });

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (recipeId != -1){
                    new MaterialAlertDialogBuilder(ViewRecipe.this)
                            .setTitle("Delete Recipe")
                            .setMessage("Do you want to delete the recipe: " + recipe.getTitle())
                            .setNegativeButton("Delete", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    if (db.deleteRecipe(recipeId)){
                                        Intent intent = new Intent(ViewRecipe.this, RecipeList.class);
                                        startActivity(intent);
                                    }

                                }
                            })
                            .setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.cancel();
                                }
                            }).show();
                }
            }
        });

        fav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new MaterialAlertDialogBuilder(ViewRecipe.this)
                        .setTitle("Edit Recipe")
                        .setMessage("Do you want to mark " + (!recipe.isFavourite() ? "as favourite recipe? " : " not favourite?"))
                        .setNeutralButton(recipe.isFavourite() ? "Mark Not Favourite" : "Mark As Favourite", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                boolean updateFav = db.updateFav(recipe.getId(), !recipe.isFavourite());
                                if (updateFav){
                                    // update recipe
                                    recipe.setFavourite(!recipe.isFavourite());

                                    // Apply the color filter
                                    String hexColorCode = recipe.isFavourite() ? "#FFD700" : "#808080";
                                    int color = Color.parseColor(hexColorCode);
                                    Drawable drawable = fav.getDrawable();
                                    drawable.setColorFilter(color, PorterDuff.Mode.SRC_IN);

                                    // Update the ImageView with the modified drawable
                                    fav.setImageDrawable(drawable);
                                }
                            }
                        }).setPositiveButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        }).show();
            }
        });

    }

    // handle back press
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

}
